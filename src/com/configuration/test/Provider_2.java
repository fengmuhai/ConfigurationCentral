package com.configuration.test;

import dna.central.httpServer.BusinessHandlerInterface;
import dna.central.httpServer.HttpServer;
import dna.central.zookeeper.client.ZkClientProvider;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.util.MessageUtils;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
 * @author fengmuhai
 * @date 2016-3-10 下午3:20:25 
 * @version 1.0  
 */
public class Provider_2 {

	public static void main(String[] args) {
		String regXmlInfo = XmlUtil.xmlFileToString("src/com/configuration/test/providerRegInfo_2.xml");
		System.out.println(regXmlInfo);
		ZkClientProvider.init(regXmlInfo);
		
		//开启http服务器
		try {
			HttpServer.init(8081, new BusinessHandler2());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class BusinessHandler2 implements BusinessHandlerInterface {

	@Override
	public Message handle(Message msg) {
		MessageUtils.setRecivedTime(msg, String.valueOf(System.currentTimeMillis()));
		System.out.println("==============调用服务-02==============");
		MessageUtils.setResponseCode(msg, "==============调用服务-02==============");
		MessageUtils.setResponseTime(msg, String.valueOf(System.currentTimeMillis()));
		return msg;
	}
	
}
