package dna.central.httpServer;

import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;

/** 
 * @author fengmuhai
 * @date 2016-2-26 上午10:44:06 
 * @version 1.0  
 */
public class BusinessHandler implements BusinessHandlerInterface {

	/**
	 * 
	 * @param msg	接收到的报文
	 * @param bhi	接口对象引用（接口传参），通过接口来实现方法传参
	 * @return		业务逻辑处理结果内容
	 */
	public static Message execute(String msg, BusinessHandlerInterface handler) {
		return handler.handle(MessageUtils.toMessage(msg));
	}


	@Override
	public Message handle(Message msg) {
		// TODO Auto-generated method stub
		return msg;
	}
}
