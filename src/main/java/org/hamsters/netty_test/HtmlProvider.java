package org.hamsters.netty_test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that provides html pages as strings.
 */
public class HtmlProvider {

    static String helloPage = "sorry";
    static String statusPage = "sorry";
    static Logger log = Logger.getLogger(HtmlProvider.class.getName());

    static {
        try {
            helloPage = readFile("pages/hello_world.html");
            statusPage = readFile("pages/status.html");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String hello() {
        return helloPage;
    }

    public String status(Statistics statistics) {
        return fillInStatusPage(statusPage, statistics);
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

    private String fillInStatusPage(String html, Statistics statistics) {
        final int requests = statistics.getRequestCount();
        final int uniqueIPCount = statistics.getUniqueIPCount();

        //TODO: optimize;
        return html
                .replace("{{requests}}", String.valueOf(requests))
                .replace("{{unique}}", String.valueOf(uniqueIPCount))
                .replace("{{connections}}", String.valueOf(statistics.getActiveConnections()))
                .replace("{{redirect_body}}", inflate(statistics.getRedirects()))
                .replace("{{request_body}}", inflate(statistics.getRequests()))
                .replace("{{connection_body}}", inflate(statistics.getLastConnections(16)));
    }

    //TODO: make inflate work with templates. It is dependent on field declaration ordering now.
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
