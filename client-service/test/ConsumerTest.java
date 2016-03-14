package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;

/** 
 * @author fengmuhai
 * @date 2016-3-1 下午3:15:27 
 * @version 1.0  
 */
public class ConsumerTest extends Thread {
	
	public int id;

	public static void main(String[] args) {
		for(int i=0;i<1;i++) {
			System.err.println("<<<<<<=========开启第"+i+"个线程==============>>>>>>>");
			new ConsumerTest(i).start();
		}
	} 
	
	public ConsumerTest(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		for(int i=0;i<5;i++){
			/*Message msg = new Message();
			msg.setTrackingNo("1000");
			msg.setSerialNo("11111");
			
			Map<String, String> serviceRecord = new HashMap<String, String>();
			serviceRecord.put("serviceUrl","");
			serviceRecord.put("serviceCode","0011");//setServiceCode("001");
			serviceRecord.put("recivedTime","");//setRecivedTime("");
			serviceRecord.put("responseTime","");//setResponseTime("");
			serviceRecord.put("serviceCode", "");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.add(serviceRecord);
			
			msg.setServiceRecords(list);
			msg.setMsgContent("Hello world!");
			   
			
			Message RespMsg = ZkClientConsumer.call(msg);
			System.out.println("=============第"+this.id+"个线程的第"+i+"次执行==========");
			System.out.println(MessageUtils.toJsonStr(RespMsg));*/
			Message msg = new Message();
			msg.setTrackingNo("1000");
			msg.setSerialNo("11111");
			
			Map<String, String> serviceRecord = new HashMap<String, String>();
			serviceRecord.put("serviceUrl","http://10.123.65.40:8080");
			serviceRecord.put("serviceCode","0011");//setServiceCode("001");
			serviceRecord.put("recivedTime","");//setRecivedTime("");
			serviceRecord.put("responseTime","");//setResponseTime("");
			serviceRecord.put("responseCode", "");
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list.add(serviceRecord);
			
			msg.setServiceRecords(list);
			msg.setMsgContent("Hello world!");
			
			System.out.println("=============第"+this.id+"个线程的第"+i+"次执行==========");
			Message RespMsg = ZkClientConsumer.call(msg);
			System.out.println(MessageUtils.toJsonStr(RespMsg));
		}
		
	}
	
}
