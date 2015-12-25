package org.hamsters.netty_test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

public class TemplateProvider {

    static String helloPage = "sorry";
    static String statusPage = "sorry";

    static {
        try {
            helloPage = readFile("pages/hello_world.html");
            statusPage = readFile("pages/status.html");
            stylesheet = readFile("pages/style.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String stylesheet;

    public String hello() {
        return helloPage;
    }

    public String status(Statistics statistics) {
        return fillIn(statusPage, statistics);
    }

    static private String readFile(String path) throws IOException {
        //oops
        return String.join("\n",
                Files.readAllLines(
                        Paths.get(
                                path
                        )
                )
        );
    }

    private String fillIn(String html, Statistics statistics) {
        final int requests = statistics.getRequestCount();
        final int uniqueIPCount = statistics.getUniqueIPCount();

        //TODO: optimize;
        final String page = html
                .replace("{{requests}}", String.valueOf(requests))
                .replace("{{unique}}", String.valueOf(uniqueIPCount))
                .replace("{{connections}}", String.valueOf(statistics.getActiveConnections()))
                .replace("{{redirect_body}}", inflate(statistics.getRedirects()))
                .replace("{{request_body}}", inflate(statistics.getRequests()))
                .replace("{{connection_body}}", inflate(statistics.getLastConnections(16)));
        return page;
    }

    private String inflate(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        for (Object o : collection) {
            final Field[] declaredFields = o.getClass().getDeclaredFields();
            sb.append("<tr>");
            for (Field declaredField : declaredFields) {
                try {
                    sb
                            .append("<td>")
                            .append(declaredField.get(o).toString())
                            .append("</td>")
                    ;
                } catch (IllegalAccessException e) {
                    sb.append("Error");
                }
            }
            sb.append("</tr>");
        }
        return sb.toString();
    }

}