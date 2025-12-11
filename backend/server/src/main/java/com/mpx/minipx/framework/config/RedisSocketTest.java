package com.mpx.minipx.framework.config;

import java.net.InetSocketAddress;
import java.net.Socket;

public class RedisSocketTest {

    public static void main(String[] args) {
        String host = "172.20.75.0";  // 그대로
        int port = 6379;

        // ★ 프록시 설정 찍어보기
        System.out.println("=== Proxy system properties ===");
        System.out.println("socksProxyHost = " + System.getProperty("socksProxyHost"));
        System.out.println("socksProxyPort = " + System.getProperty("socksProxyPort"));
        System.out.println("http.proxyHost  = " + System.getProperty("http.proxyHost"));
        System.out.println("https.proxyHost = " + System.getProperty("https.proxyHost"));
        System.out.println("java.net.useSystemProxies = " + System.getProperty("java.net.useSystemProxies"));

        System.out.println("\n=== RAW SOCKET TEST (No Spring) ===");
        System.out.println("Connecting to " + host + ":" + port);

        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 3000);
            System.out.println(">>> Connected OK from pure Java");
        } catch (Exception e) {
            System.out.println(">>> RAW SOCKET ERROR:");
            e.printStackTrace();
        }
    }
}

