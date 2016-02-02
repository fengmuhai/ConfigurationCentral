package test;

import java.util.HashMap;

import dna.central.zookeeper.client.util.JsonTools;

public class JsonToolsTest {

	public static void main(String[] args) {
		//String jsonStr = XmlUtil.xmlFileToString("client-service/jsontest.xml");
		String jsonStr = "{\"name\":\"fengmuhai\",\"age\":23}";
		HashMap<String, Object> message = (HashMap<String, Object>) JsonTools.parseJSON2Map(jsonStr);
		for(String key:message.keySet()) {
			System.out.println(message.get(key));
		}
	}
}
