package dna.central.zookeeper.test;

import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.ZkClientProvider;
import dna.central.zookeeper.client.util.XmlUtil;

public class ClientServiceDemo {

	public void service_init(String[] args) {
		//提供方注册
		String provideRegInfo = XmlUtil.xmlFileToString("提供方xml注册文件路径");	
		ZkClientProvider.init(provideRegInfo);
		//消费方注册
		String consumerRegInfo = XmlUtil.xmlFileToString("消费方xml注册文件路径");	
		ZkClientConsumer.init(consumerRegInfo);

	}
	
	
	
}
