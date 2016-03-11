package com.configuration.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
 * @author fengmuhai
 * @date 2016-3-10 下午3:17:13 
 * @version 1.0  
 */
public class Consumer {
	
	public static void main(String[] args) {
		String conXmlInfo = XmlUtil.xmlFileToString("src/com/configuration/test/consumerRegInfo.xml");
		ZkClientConsumer.init(conXmlInfo);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
		   
		Message RespMsg = ZkClientConsumer.call(msg);
		System.out.println(MessageUtils.toJsonStr(RespMsg));
	}

}
