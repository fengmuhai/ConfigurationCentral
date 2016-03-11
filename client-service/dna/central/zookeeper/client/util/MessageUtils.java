package dna.central.zookeeper.client.util;

import java.util.List;
import java.util.Map;

import dna.central.zookeeper.client.entity.Message;

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
	
	//消息对象转换为字符串
	public static String toJsonStr(Message msg) {
		return JacksonTools.object2Json(msg);
	}
	
	/**
	 * 为服务提供者提供设置接收时间
	 * @param message
	 * @param recivedTime
	 */
	public static void setRecivedTime(Message message, String recivedTime) {
		if(message==null) {
			System.err.println("Message is null!");
			return;
		}
		
		Map<String, String> record = getLatelyRecord(message);
		record.put("recivedTime",recivedTime);
	}
	
	/**
	 * 为服务消费者提供设置响应时间的
	 * @param message
	 * @param responseTime
	 */
	public static void setResponseTime(Message message, String responseTime) {
		if(message==null) {
			System.err.println("Message is null!");
			return;
		}
		Map<String, String> record = getLatelyRecord(message);
		record.put("responseTime",responseTime);
	}
	
	/**
	 * 为服务消费者提供设置响应码
	 * @param message
	 * @param responseTime
	 */
	public static void setResponseCode(Message message, String responseCode) {
		if(message==null) {
			System.err.println("Message is null!");
			return;
		}
		Map<String, String> record = getLatelyRecord(message);
		record.put("responseCode",responseCode);
	}
	
	
	
	/**
	 * 获取指定下标的消息记录
	 * @param msg
	 * @param index
	 * @return
	 */
	public static Map<String, String> getServiceRecord(Message msg, int index) {
		return msg.getServiceRecords().get(index);
	}
	
	/**
	 * 获取最近的消息记录
	 * @param msg
	 * @return
	 */
	public static Map<String, String> getLatelyRecord(Message message) {
		List<Map<String, String>> list = message.getServiceRecords();
		if(list.size()<=0 || list.isEmpty()){
			System.err.println("Message serviceRecords is null!");
			return null;
		}
		return list.get(list.size()-1);
	}
	
}
