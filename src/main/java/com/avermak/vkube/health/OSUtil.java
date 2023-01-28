package com.avermak.vkube.health;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class OSUtil {
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    public static boolean isLinuxOS() {
        return "linux".equalsIgnoreCase(getOSName());
    }
    public static boolean isMacOS() {
        return "mac os x".equalsIgnoreCase(getOSName());
    }
    public static boolean isWindowsOS() {
        return false;
    }

    public static int getCPUCount() {
        try {
            String cpustr = "-1";
            if (isLinuxOS() || isMacOS()) {
                cpustr = runOSCommandAndGetOutput(new String[]{"getconf", "_NPROCESSORS_ONLN"});
            }
            return Integer.parseInt(cpustr.trim());
        } catch (Exception ex) {
            System.out.println("Error getting CPU count. " + ex);
            ex.printStackTrace();
            return -1;
        }
    }
    public static double getCPUTemperature() {
        try {
            if (isLinuxOS()) {
                String cpustr = runOSCommandAndGetOutput(new String[]{"cat", "/sys/class/thermal/thermal_zone0/temp"});
                return Integer.parseInt(cpustr.trim())/1000.0;
            }
        } catch (Exception ex) {
            System.out.println("Error getting CPU temperature. " + ex);
            //ex.printStackTrace();
        }
        return 0.0;
    }

    /**
     * @return Returns two objects. The first is a double[] containing two values which
     *  are CPU usage % values as %user, %system respectively. The second is an int[] that
     *  has two values - total memory and free memory (in MB).
     */
    public static Object[] getCPUAndMemoryUsage() {
        Object[] cpumem = null;
        try {
            if (isLinuxOS()) {
                String topStr = runOSCommandAndGetOutput(new String[]{"top", "-bn1"});
                System.out.println("TOP (linux): " + topStr);
                cpumem = parseTopOutputLinux(topStr);
            } else if (isMacOS()) {
                String topStr = runOSCommandAndGetOutput(new String[]{"top", "-l1", "-n0"});
                System.out.println("TOP (macos): " + topStr);
                cpumem = parseTopOutputMacOS(topStr);
            }
        } catch (Exception ex) {
            System.out.println("Error getting CPU and Memory usage. " + ex);
            ex.printStackTrace();
        }
        if (cpumem == null) {
            cpumem = new Object[]{new double[0], new int[]{0, 0}};
        }
        return cpumem;
    }

    private static Object[] parseTopOutputLinux(String top) throws Exception {
        double[] cpudata = new double[2];
        int[] memdata = new int[2];
        BufferedReader br = new BufferedReader(new StringReader(top));
        String line = null;
        String cpuHeader = "%cpu(s):";
        String memHeader = "mib mem :";
        while ((line = br.readLine()) != null) {
            if (line.toLowerCase().startsWith(cpuHeader)) {
                line = line.substring(cpuHeader.length() + 1);
                String valstr = line.substring(0, line.indexOf(" us,")).trim();
                cpudata[0] = Double.parseDouble(valstr);
                line = line.substring(line.indexOf(" us,") + 4);
                valstr = line.substring(0, line.indexOf(" sy,")).trim();
                cpudata[1] = Double.parseDouble(valstr);
            }
            if (line.toLowerCase().startsWith(memHeader)) {
                line = line.substring(memHeader.length() + 1);
                String valstr = line.substring(0, line.indexOf(" total,")).trim();
                memdata[0] = (int) Math.round(Double.parseDouble(valstr));
                line = line.substring(line.indexOf(" total,") + 7);
                valstr = line.substring(0, line.indexOf(" free,")).trim();
                memdata[1] = (int) Math.round(Double.parseDouble(valstr));
            }
        }
        return new Object[]{cpudata, memdata};
    }
    private static Object[] parseTopOutputMacOS(String top) throws Exception {
        double[] cpudata = new double[2];
        int[] memdata = new int[2];
        BufferedReader br = new BufferedReader(new StringReader(top));
        String line = null;
        String cpuHeader = "cpu usage: ";
        String memHeader = "physmem: ";
        while ((line = br.readLine()) != null) {
            if (line.toLowerCase().startsWith(cpuHeader)) {
                StringTokenizer st = new StringTokenizer(line.substring(cpuHeader.length()), ",");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken().trim();
                    if (token.length() == 0) {
                        continue;
                    }
                    double val = 0.0;
                    try { val = Double.parseDouble(token.substring(0, token.indexOf(' ') - 1)); } catch (Exception ex) {}
                    if (token.endsWith("user")) {
                        cpudata[0] = val;
                    }
                    if (token.endsWith("sys")) {
                        cpudata[1] = val;
                    }
                }
            }
            if (line.toLowerCase().startsWith(memHeader)) {
                line = line.substring(memHeader.length()).trim();
                String memUsedStr = line.substring(0, line.indexOf(' ')).trim();
                char memUnit = memUsedStr.charAt(memUsedStr.length() - 1);
                int memUsed = Integer.parseInt(memUsedStr.substring(0, memUsedStr.length() - 1));
                if (memUnit == 'G') {
                    memUsed = 1024 * memUsed;
                }
                String memFreeStr = line.substring(line.lastIndexOf(',') + 1).trim();
                memFreeStr = memFreeStr.substring(0, memFreeStr.indexOf(' '));
                memUnit = memFreeStr.charAt(memFreeStr.length() - 1);
                int memFree = Integer.parseInt(memFreeStr.substring(0, memFreeStr.length() - 1));
                if (memUnit == 'G') {
                    memFree = 1024 * memFree;
                }
                memdata[0] = memUsed + memFree;
                memdata[1] = memFree;
            }
        }
        return new Object[]{cpudata, memdata};
    }

    public static String runOSCommandAndGetOutput(String[] cmd) throws Exception {
        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream istream = proc.getInputStream();
        proc.waitFor(3000, TimeUnit.MILLISECONDS);
        String sout = new String(istream.readAllBytes(), StandardCharsets.UTF_8);
        istream.close();
        return sout;
    }
}
