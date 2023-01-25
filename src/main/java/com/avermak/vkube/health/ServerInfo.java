package com.avermak.vkube.health;

public class ServerInfo {
    private String podName = null;
    private String podIP = null;
    private String nodeName = null;
    private String nodeIP = null;
    private long podTime = 0;
    private double[] cpuTemperatures;
    private long totalMemory = 0;
    private long freeMemory = 0;
    private double[] cpuUsage;
    private int cpuCores;

    private String error = "";

    public ServerInfo() {}

    public ServerInfo(String podName, String podIP, String nodeName, String nodeIP, long podTime, int cpuCores) {
        this.podName = podName;
        this.podIP = podIP;
        this.nodeName = nodeName;
        this.nodeIP = nodeIP;
        this.podTime = podTime;
        this.cpuCores = cpuCores;
        this.cpuTemperatures = new double[cpuCores];
        this.cpuUsage = new double[cpuCores];
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getPodIP() {
        return podIP;
    }

    public void setPodIP(String podIP) {
        this.podIP = podIP;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public long getPodTime() {
        return podTime;
    }

    public void setPodTime(long podTime) {
        this.podTime = podTime;
    }

    public double[] getCPUTemperatures() {
        return cpuTemperatures;
    }

    public void setCPUTemperatures(double[] cpuTemperatures) {
        this.cpuTemperatures = cpuTemperatures;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public double[] getCPUUsage() {
        return cpuUsage;
    }

    public void setCPUUsage(double[] cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public int getCPUCores() {
        return cpuCores;
    }

    public void setCPUCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
