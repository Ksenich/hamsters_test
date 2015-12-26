package org.hamsters.netty_test;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

import java.util.logging.Logger;

public class HttpHandler extends ChannelInboundHandlerAdapter {
    private final String ip;
    int sentBytes;
    int receivedBytes;
    float speed;
    long connectionStart;

    Statistics statistics;

    public void setStatistics(Statistics statistics) {
        if (statistics == null)
            throw new NullPointerException("Status monitoring is required");
        this.statistics = statistics;
    }

    Logger log = Logger.getLogger(HttpHandler.class.getName());
    private String uri;

    public HttpHandler(String ip, Statistics statistics) {
        this.ip = ip;
        setStatistics(statistics);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel registered for " + ip);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active for " + ip);
        statistics.openConnection();
        connectionStart = System.currentTimeMillis();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final String msgString = msg.toString();
        log.info("Channel read for " + ip + ": " + msgString);
        receivedBytes += msgString.getBytes().length;
        if (!(msg instanceof HttpRequest))
            return;
        HttpRequest http = (HttpRequest) msg;
        uri = http.getUri();
        final Controller controller = new Controller(statistics);
        FullHttpResponse response = controller.getResponse(ip, uri);

        sentBytes = response.content().writerIndex();
        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        final float connectionTime = (System.currentTimeMillis() - connectionStart) / 1000f;
        speed = (sentBytes + receivedBytes) / connectionTime;
        statistics.closeConnection(ip, uri, sentBytes, receivedBytes, speed);
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel Inactive for " + ip);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel unregistered for " + ip);
    }
}
