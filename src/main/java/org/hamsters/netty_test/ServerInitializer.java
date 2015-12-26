package org.hamsters.netty_test;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private Context context = new Context();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        String ip = socketChannel.remoteAddress().getHostString();
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        final HttpHandler handler = new HttpHandler(ip, context.getStatistics());
        pipeline.addLast("handler", handler);
    }
}
