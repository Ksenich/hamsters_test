package org.hamsters.netty_test;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

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

    private String uri;

    public HttpHandler(String ip, Statistics statistics) {
        this.ip = ip;
        setStatistics(statistics);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        statistics.openConnection();
        connectionStart = System.nanoTime();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final String msgString = msg.toString();
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
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final float connectionTime = (System.nanoTime() - connectionStart) / 1_000_000_000f;
        speed = (sentBytes + receivedBytes) / connectionTime;
        statistics.closeConnection(ip, uri, sentBytes, receivedBytes, speed);
    }

}
