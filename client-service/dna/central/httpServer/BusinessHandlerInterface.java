package dna.central.httpServer;

import dna.central.zookeeper.client.entity.Message;

/** 
 * @author fengmuhai
 * @date 2016-2-26 上午10:15:51 
 * @version 1.0  
 */
public interface BusinessHandlerInterface {		//业务逻辑处理接口

	Message handle(Message msg);	//msg为接收到的请求消息
	
	
}
