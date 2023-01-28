package com.avermak.vkube.health;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.net.InetAddress;

public class MainVerticle extends AbstractVerticle {

    public static final String CONTEXT_PATH = "/health";
    public static final int SERVICE_PORT = 22100;

    private ServerInfo serverInfo = null;

    @Override
    public void start() throws Exception {
        System.out.println("Starting vkube-health");

        System.out.println("Creating http server instance");
        HttpServer httpServer = vertx.createHttpServer();

        System.out.println("Creating Router configuration");
        Router router = Router.router(vertx);
        Route apiRoute = router.route().path(CONTEXT_PATH);

        apiRoute.handler(ctx -> {
            System.out.println("Received request over http. " + ctx.request().absoluteURI());
            ctx.response().end(buildResponse(ctx.request()));
        });
        System.out.println("API route configured at " + CONTEXT_PATH);
        System.out.println("Attaching router to server");
        httpServer.requestHandler(router);
        System.out.println("Configuring gRPC handler routine");
        System.out.println("Starting server on port " + SERVICE_PORT);
        httpServer.listen(SERVICE_PORT, ares -> {
            if (ares.failed()) {
                System.out.println("Failed to start server on port "+SERVICE_PORT+". "+ ares.cause());
                System.err.println("Failed to start server on port "+SERVICE_PORT+". "+ ares.cause());
                System.out.println("Closing vertx");
                vertx.close();
            } else {
                System.out.println("Server started on " + ares.result().actualPort());
            }
        });
    }

    private String buildResponse(HttpServerRequest request) {
        Gson g = new Gson();
        return g.toJson(getServerInfo());
    }

    private ServerInfo getServerInfo() {
        if (this.serverInfo == null) {
            synchronized (this) {
                if (this.serverInfo == null) {
                    this.serverInfo = new ServerInfo();
                    String podName = "unavailable";
                    try {
                        podName = InetAddress.getLocalHost().getHostName();
                        this.serverInfo.setPodName(podName);
                    } catch (Exception ex) {
                        System.out.println("Unable to retrieve podName. " + ex);
                        ex.printStackTrace();
                    }
                    String podIP = "0.0.0.0";
                    try {
                        podIP = InetAddress.getLocalHost().getHostAddress();
                        this.serverInfo.setPodIP(podIP);
                    } catch (Exception ex) {
                        System.out.println("Unable to retrieve podIP. " + ex);
                        ex.printStackTrace();
                    }
                    String nodeName = System.getenv("NODE_NAME");
                    if (nodeName == null) {
                        nodeName = "unavailable";
                    }
                    String nodeIP = System.getenv("NODE_IP");
                    if (nodeIP == null) {
                        nodeIP = "0.0.0.0";
                    }
                    this.serverInfo.setNodeName(nodeName);
                    this.serverInfo.setNodeIP(nodeIP);
                    this.serverInfo.setCPUCores(OSUtil.getCPUCount());
                }
            }
        }
        this.serverInfo.setPodTime(System.currentTimeMillis());
        this.serverInfo.setCPUTemperatures(new double[] {OSUtil.getCPUTemperature()});
        Object[] cpuMem = OSUtil.getCPUAndMemoryUsage();
        this.serverInfo.setCPUUsage((double[])cpuMem[0]);
        this.serverInfo.setTotalMemory(((int[])cpuMem[1])[0]);
        this.serverInfo.setFreeMemory(((int[])cpuMem[1])[1]);
        return this.serverInfo;
    }
}