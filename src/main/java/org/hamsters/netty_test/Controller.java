package org.hamsters.netty_test;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Cause it's roughly takes place of controller in MVC frameworks I worked with;
 */
public class Controller {
    private final TemplateProvider templateProvider = new TemplateProvider();
    private Statistics statistics;

    public void setStatistics(Statistics statistics) {
        if(statistics == null)
            throw new NullPointerException("Status monitoring is required");
        this.statistics = statistics;
    }

    public Controller(Statistics statistics){
        setStatistics(statistics);
    }

    //TODO: move statistics gathering to its own class.
    public FullHttpResponse getResponse(String ip, String uri) {
        if (uri.startsWith("/hello")) {
            statistics.request(ip);
            return helloWorld();
        }
        if (uri.startsWith("/status")) {
            statistics.request(ip);
            return status();
        }
        if (uri.startsWith("/redirect")) {
            RedirectURL ruri = new RedirectURL(uri);
            if (!ruri.isValid())
                return error404();
            String address = ruri.getAddress();
            statistics.redirect(ip, address);
            return redirect(address);
        }
        return error404();
    }

    private FullHttpResponse redirect(String to) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, TEMPORARY_REDIRECT);
        //TODO: find revelant constant
        response.headers().set("Location", to);
        return response;
    }

    private FullHttpResponse error404() {
        return new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
    }

    private FullHttpResponse status() {
        String status = templateProvider.status(statistics);
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(status, CharsetUtil.UTF_8));
    }

    private FullHttpResponse helloWorld() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException ignored) {

        }
        String hello = templateProvider.hello();
        return new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(hello, CharsetUtil.UTF_8));
    }

}
