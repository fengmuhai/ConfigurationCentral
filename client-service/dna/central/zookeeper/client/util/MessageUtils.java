package dna.central.zookeeper.client.util;

import java.util.List;

import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.entity.ServiceRecord;

public class MessageUtils {

	//private Message message;
	
	//将字符串转化为消息对象
	public static Message toMessage(String msg) {
		if(msg==null || msg.equals("")) {
			System.err.println("Message String is null!");
			return null;
		}
		Message message = JacksonTools.json2Object(msg, Message.class);
		return message;
		
	}
	
	/**
	 * 为服务提供者提供设置接收时间的方法
	 * @param message
	 * @param recivedTime
	 */
	public static void setRecivedTime(Message message, String recivedTime) {
		if(message==null) {
			System.err.println("Message is null!");
			return;
		}
		List<ServiceRecord> list = message.getServiceRecords();
		if(list.size()<=0 || list.isEmpty()){
			System.err.println("Message serviceRecords is null!");
			return;
		}
		ServiceRecord sr = list.get(list.size()-1);
		sr.setRecivedTime(recivedTime);
	}
	
	/**
	 * 为服务消费者提供设置响应时间的方法
	 * @param message
	 * @param responseTime
	 */
	public static void setResponseTime(Message message, String responseTime) {
		if(message==null) {
			System.err.println("Message is null!");
			return;
		}
		List<ServiceRecord> list = message.getServiceRecords();
		if(list.size()<=0 || list.isEmpty()){
			System.err.println("Message serviceRecords is null!");
			return;
		}
		ServiceRecord sr = list.get(list.size()-1);
		sr.setRecivedTime(responseTime);
	}
	
}
