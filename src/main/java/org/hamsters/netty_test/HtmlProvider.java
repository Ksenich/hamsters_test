package org.hamsters.netty_test;

import java.io.IOException;
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
                .replace("{{redirect_table}}", getRedirectsTable(statistics.getRedirects()))
                .replace("{{request_table}}", getRequestsTable(statistics.getRequests()))
                .replace("{{connection_table}}", getLastConnectionsTable(statistics.getLastConnections()));
    }

    private String getRedirectsTable(Collection<Statistics.RedirectInfo> ri) {
        StringBuilder sb = new StringBuilder();
        String[] headers = {"URL", "count"};
        sb.append("<table>").append("<tr>");
        for (String s : headers) {
            sb.append("<th>").append(s).append("</th>");
        }
        sb.append("</tr>");
        for (Statistics.RedirectInfo info : ri) {
            sb.append("<tr>");
            sb.append("<td>").append(info.url).append("</td>");
            sb.append("<td>").append(info.count).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String getRequestsTable(Collection<Statistics.RequestsInfo> ri) {
        StringBuilder sb = new StringBuilder();
        String[] headers = {"ip", "count", "last"};
        sb.append("<table>").append("<tr>");
        for (String s : headers) {
            sb.append("<th>").append(s).append("</th>");
        }
        sb.append("</tr>");
        for (Statistics.RequestsInfo info : ri) {
            sb.append("<tr>");
            sb.append("<td>").append(info.ip).append("</td>");
            sb.append("<td>").append(info.count).append("</td>");
            sb.append("<td>").append(info.last).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String getLastConnectionsTable(Collection<Statistics.ConnectionInfo> ri) {
        StringBuilder sb = new StringBuilder();
        String[] headers = {"Source IP", "Uri", "Timestamp", "sent(B)", "received(B)", "speed(B/s)"};
        sb.append("<table>").append("<tr>");
        for (String s : headers) {
            sb.append("<th>").append(s).append("</th>");
        }
        sb.append("</tr>");
        for (Statistics.ConnectionInfo info : ri) {
            sb.append("<tr>");
            sb.append("<td>").append(info.ip).append("</td>");
            sb.append("<td>").append(info.uri).append("</td>");
            sb.append("<td>").append(info.timestamp).append("</td>");
            sb.append("<td>").append(info.sent).append("</td>");
            sb.append("<td>").append(info.received).append("</td>");
            sb.append("<td>").append(info.speed).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

}
