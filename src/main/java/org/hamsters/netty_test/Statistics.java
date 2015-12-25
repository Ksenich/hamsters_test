package org.hamsters.netty_test;

import java.util.*;

public class Statistics {
    //TODO: replace by AtomicInt?
    int activeConnections = 0;
    private int requestCount = 0;
    private int uniqueIPCount = 0;

    Map<String, RedirectInfo> redirects = new HashMap<String, RedirectInfo>();
    Map<String, RequestsInfo> requests = new HashMap<String, RequestsInfo>();
    List<ConnectionInfo> connections = new ArrayList<ConnectionInfo>();

    public void openConnection() {
        activeConnections++;
    }

    public void closeConnection(String ip, String uri, int sentBytes, int receivedBytes, float speed) {
        final ConnectionInfo ci = new ConnectionInfo();
        ci.ip = ip;
        ci.uri = uri;
        ci.sent = sentBytes;
        ci.received = receivedBytes;
        ci.speed = speed;
        ci.timestamp = new Date();
        connections.add(ci);
        activeConnections--;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getUniqueIPCount() {
        return uniqueIPCount;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void redirect(String ip, String to) {
        request(ip);
        if (redirects.containsKey(to))
            redirects.get(to).incrementCount();
        else
            redirects.put(to, new RedirectInfo(to));
    }

    public void request(String ip) {
        requestCount++;
        if (requests.containsKey(ip))
            requests.get(ip).incrementCount();
        else {
            requests.put(ip, new RequestsInfo(ip, new Date()));
            uniqueIPCount++;
        }
    }

    public Collection<RedirectInfo> getRedirects() {
        return redirects.values();
    }

    public Collection<ConnectionInfo> getLastConnections(int limit) {
        final int last = connections.size();
        final int first = Math.max(last - 1 - limit, 0);
        return connections.subList(first, last);
    }

    public Collection<RequestsInfo> getRequests() {
        return requests.values();
    }

    public static class RedirectInfo {
        String url;
        int count;

        public RedirectInfo(String url, int count) {
            this.url = url;
            this.count = count;
        }

        public RedirectInfo(String url) {
            this(url, 1);
        }

        public void incrementCount() {
            count++;
        }
    }

    public static class RequestsInfo {
        String ip;
        int count;
        Date last;

        public RequestsInfo(String ip, Date last) {
            this.last = last;
            count = 0;
            this.ip = ip;
        }

        public void incrementCount() {
            count++;
            last = new Date();
        }
    }

    public static class ConnectionInfo {
        String ip;
        String uri;
        Date timestamp;
        int sent;
        int received;
        float speed;
    }
}
