package com.configuration.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.ConsumerTest;

import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
 * @author fengmuhai
 * @date 2016-3-10 下午3:17:13 
 * @version 1.0  
 */
public class ConsumerThread extends Thread {
	
	private int id;
	
	public ConsumerThread(int id) {
		this.id = id;
	}
	
	public static void main(String[] args) {
		String conXmlInfo = XmlUtil.xmlFileToString("src/com/configuration/test/consumerRegInfo.xml");
		ZkClientConsumer.init(conXmlInfo);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<30;i++) {
			System.err.println("<<<<<<=========开启第"+i+"个线程==============>>>>>>>");
			new ConsumerThread(i).start();
		}
		
	}
	
	public void run() {
		for(int i=0;i<2;i++) {
			//调用服务
			Message msg = new Message();
			msg.setTrackingNo("1000");
			msg.setSerialNo("11111");
			
			Map<String, String> serviceRecord = new HashMap<String, String>();
			serviceRecord.put("serviceUrl","");
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
