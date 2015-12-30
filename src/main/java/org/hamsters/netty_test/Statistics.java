package org.hamsters.netty_test;

import java.util.*;

/**
 * Class for monitoring server status.
 */
public class Statistics {
    int activeConnections = 0;
    private int requestCount = 0;
    private int uniqueIPCount = 0;

    Map<String, RedirectInfo> redirects = new HashMap<>();
    Map<String, RequestsInfo> requests = new HashMap<>();
    List<ConnectionInfo> connections = new ArrayList<>();

    /**
     * Increase open connections number.
     */
    public synchronized void openConnection() {
        activeConnections++;
    }


    /**
     * Decrease open connections number and add information about connection to closed connections list.
     * @param ip requester ip.
     * @param uri requested url
     * @param sentBytes bytes sent.
     * @param receivedBytes bytes received.
     * @param speed connection speed.
     */
    public synchronized void closeConnection(String ip, String uri, int sentBytes, int receivedBytes, float speed) {
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

    public synchronized void redirect(String ip, String to) {
        request(ip);
        if (redirects.containsKey(to))
            redirects.get(to).incrementCount();
        else
            redirects.put(to, new RedirectInfo(to));
    }

    public synchronized void request(String ip) {
        requestCount++;
        if (requests.containsKey(ip))
            requests.get(ip).incrementCount();
        else {
            requests.put(ip, new RequestsInfo(ip, new Date()));
            uniqueIPCount++;
        }
    }

    public synchronized Collection<RedirectInfo> getRedirects() {
        ArrayList<RedirectInfo> copy = new ArrayList<>(redirects.size());
        copy.addAll(redirects.values());
        return copy;
    }

    public synchronized Collection<ConnectionInfo> getLastConnections(int limit) {
        final int last = connections.size();
        final int first = Math.max(last - 1 - limit, 0);
        ArrayList<ConnectionInfo> copy = new ArrayList<>(last - first);
        copy.addAll(connections.subList(first, last));
        return copy;
    }

    public synchronized Collection<RequestsInfo> getRequests() {
        return requests.values();
    }

    public static class RedirectInfo {
        String url;
        int count;

        private RedirectInfo(String url, int count) {
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
            count = 1;
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
