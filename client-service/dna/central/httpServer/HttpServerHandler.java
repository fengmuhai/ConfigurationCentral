package dna.central.httpServer;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;

//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!' };
    private static Object businessHandler = null;
    
    public HttpServerHandler(Object businessHandler) {
    	if(HttpServerHandler.businessHandler==null) {
        	HttpServerHandler.businessHandler = businessHandler;
    	}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	/*if (msg instanceof HttpContent) {  
            HttpContent httpContent = (HttpContent) msg;  
            ByteBuf content = httpContent.content();  
            byte[] result1 = new byte[content.readableBytes()];
            content.readBytes(result1);
            String resultStr = new String(result1);
            System.out.println("Client said:" + resultStr);
            }  */
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            
            //获取消息内容
            HttpContent httpContent = (HttpContent) msg;  
            ByteBuf content = httpContent.content();  
            byte[] result1 = new byte[content.readableBytes()];
            content.readBytes(result1);
            String resultStr = new String(result1);
            //System.out.println("Client said:" + resultStr);
            
            
            
            /**
             * 增加业务逻辑处理方法
             */
            //String resp = BusinessHandler.execute(req.toString(), (BusinessHandlerInterface) businessHandler);
            
            /**
             * 直接使用接口引用指向该对象
             */
            BusinessHandlerInterface handler = (BusinessHandlerInterface) businessHandler;
            Message respMsg = handler.handle(MessageUtils.toMessage(resultStr));
            String resp = MessageUtils.toJsonStr(respMsg);
           
            
            //FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resp.getBytes()));
            
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}