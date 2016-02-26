package dna.central.httpServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final Object businessHandler;
    

    public HttpServerInitializer(SslContext sslCtx, Object businessHandler) {
        this.sslCtx = sslCtx;
        this.businessHandler = businessHandler;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast("decoder", new HttpRequestDecoder(8192, 8192 * 2,
                8192 * 2));
        p.addLast("inflater", new HttpContentDecompressor());
        p.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast(new HttpServerHandler(businessHandler));
    }
}