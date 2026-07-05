package dev.elayachi.server;

import jakarta.xml.ws.Endpoint;

public class ServerJWS {
    public static void main(String[] args) {
        String url = "http://0.0.0.0:9090/BanqueWS";
        Endpoint.publish(url, new BanqueService());
        System.out.println("Web service deploye sur " + url);
        System.out.println("WSDL : http://localhost:9090/BanqueWS?wsdl");
    }
}
