package test;

import dna.central.zookeeper.client.entity.ProviderRegInfo;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
 * @author fengmuhai
 * @date 2016-3-3 上午9:55:12 
 * @version 1.0  
 */
public class XmlParseObjectTest {
	public static void main(String[] args) {
		String xmlString = XmlUtil.xmlFileToString("client-service/providerRegInfo.xml");
		ProviderRegInfo pr = XmlUtil.toBean(xmlString, ProviderRegInfo.class);
		System.out.println(XmlUtil.toXml(pr));
	}

}
