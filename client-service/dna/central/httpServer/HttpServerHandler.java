package dna.central.httpServer;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.omg.PortableServer.THREAD_POLICY_ID;

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
    public static final String content = "{\"trackingNo\":\"1000\",\"serialNo\":\"11111\",\"serviceRecords\":[{\"responseCode\":\"000\",\"recivedTime\":\"1457673069009\",\"serviceCode\":\"0011\",\"serviceUrl\":\"\",\"responseTime\":\"1457673074019\"}],\"msgContent\":\"Hello world!\"}";
    private static Object businessHandler = null;
    private static final int MAX_HANDLER = 10;
    private static int current_request = 0;
    private static ExecutorService executor = Executors.newFixedThreadPool(5);

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
            System.out.println("当前线程："+Thread.currentThread().getName());
            //获取消息
            String msgContent = getMessageContent(msg);
            
            
            HandlerTask task = new HandlerTask(businessHandler, msgContent);
            Future<Message> message = executor.submit(task);
            String resp = null;
			try {
				resp = MessageUtils.toJsonStr(message.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
            //executor.shutdown();
            
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resp.getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }
            
            
            //消息请求处理
            /*String resp = messageHandler(msgContent);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resp.getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }*/
           
            
            
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    public static String messageHandler(String msgContent) {
        /**
         * 增加业务逻辑处理方法
         */
        //String resp = BusinessHandler.execute(req.toString(), (BusinessHandlerInterface) businessHandler);
        
        //直接使用接口引用指向该对象
        BusinessHandlerInterface handler = (BusinessHandlerInterface) businessHandler;
        Message respMsg = handler.handle(MessageUtils.toMessage(msgContent));
        return MessageUtils.toJsonStr(respMsg);
    }
    
    public static String getMessageContent(Object msg) {
    	//获取消息内容
        HttpContent httpContent = (HttpContent) msg;  
        ByteBuf content = httpContent.content();  
        byte[] result1 = new byte[content.readableBytes()];
        content.readBytes(result1);
        content.release();
        return new String(result1);
    }
    
    public static boolean accept() {
    	if(get_current_request()<=MAX_HANDLER)
    		return true;
    	return false;
    }

	public static synchronized int get_current_request() {
		return current_request;
	}
	
	public static synchronized void inc_current_request() {
		current_request++;
	}
	
	public static synchronized void dec_current_request() {
		current_request--;
	}

    
}

class HandlerTask implements Callable<Message> {
	
	Object businessHandler;
	String msgContent;
    
	public HandlerTask(Object businessHandler, String msgContent) {
		this.businessHandler = businessHandler;
		this.msgContent = msgContent;
	}

	@Override
	public Message call() throws Exception {
		System.out.println("当前业务逻辑-线程："+Thread.currentThread().getName());
		//直接使用接口引用指向该对象
        BusinessHandlerInterface handler = (BusinessHandlerInterface) businessHandler;
        Message respMsg = handler.handle(MessageUtils.toMessage(msgContent));
        return respMsg;
	}
}


