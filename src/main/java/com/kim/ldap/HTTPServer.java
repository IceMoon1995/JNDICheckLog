package com.kim.ldap;


import com.kim.ldap.utils.Config;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

import static com.kim.ldap.LdapServer.mmList;

public class HTTPServer {

    public static void start() throws IOException {

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(Config.httpPort), 0);
        httpServer.createContext("/", new HttpHandler() {
            public void handle(HttpExchange httpExchange) {
                try {
                    System.out.println("[+] New HTTP Request From " + httpExchange.getRemoteAddress() + "  " + httpExchange.getRequestURI());

                    String path = httpExchange.getRequestURI().getPath();
                    if (path.endsWith(".md5")) {
                        handleClassRequest(httpExchange);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        httpExchange.close();
                        System.out.println("[!] Response Code: " + 404);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        httpServer.setExecutor(null);
        httpServer.start();
        System.out.println("[+] HTTP Server Start Listening on " + Config.httpPort + "...");
    }

    private static void handleClassRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String className = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        System.out.println("[+] Receive ClassRequest: " + className + ".md5");
        if (mmList.contains(className)) {
            System.out.println("[+] Response Code: " + 200);
            exchange.sendResponseHeaders(200, 0);
        } else {
            System.out.println("[!] Response Code: " + 404);
            exchange.sendResponseHeaders(404, 0);
        }
        exchange.close();
    }


    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<String, String>();

        try {
            for (String str : query.split("&")) {
                try {
                    String[] parts = str.split("=", 2);
                    params.put(parts[0], parts[1]);
                } catch (Exception e) {
                    //continue
                }
            }
        } catch (Exception e) {
            //continue
        }

        return params;
    }

}
