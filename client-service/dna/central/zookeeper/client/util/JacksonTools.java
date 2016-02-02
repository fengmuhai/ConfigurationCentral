package dna.central.zookeeper.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class JacksonTools {
	
	public static void main(String[] args) {
		//String s = "{ \"name\" : \"萧远山\", \"sex\" : \"男\", \"age\" : \"23\",\"address\" : [{\"sheng\":\"河南\",\"shengy\":\"河北\"},{\"shi\":\"郑州\",\"shiy\":\"石家庄\"}]}";
		/*String jstr = JacksonTools.jsonFileToString("client-service/message.json");
		Map<?, ?> map = jsonStr2Map(jstr);
		List list = (List)map.get("service-records");
		//String str = "{\"002\",\"service-url\": \"10.123.65.55:8080\",\"recived-time\": \"143298647392\",\"response-time\": \"143567983349\"}";
		String str = "{service-code=002, service-url=10.123.65.55:8080, recived-time=143298647392, response-time=143567983349}";
		list.add(str);
		System.out.println(toString(map));*/
		//System.out.println(map2JsonStr(map));
	}
	
	/**
	 * 将json字符串转为对象
	 * @param jsonStr	json字符串
	 * @param cls		要转化的目的类对象
	 * @return
	 */
	public static <T> T json2Object(String jsonStr, Class<T> cls) {
		if(jsonStr==null || jsonStr.equals("")) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		T t = null;
		try {
			t = (T) mapper.readValue(jsonStr, cls);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static String object2Json(Object obj) {
		if(obj==null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStr;
	}
 
	/**
	 * json字符串转Map对象
	 * @param jsonStr
	 * @return
	 */
	public static Map<?,?> jsonStr2Map(String jsonStr) {
		if(jsonStr==null || jsonStr.equals("")) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		Map<?, ?> map = null;
		try {
			map = mapper.readValue(jsonStr, Map.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	} 
	
	/**
	 * Map对象转String字符串
	 * @param map
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String map2JsonStr(Map<?, ?> map) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(map);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return jsonStr;
		}
	}
	
	/**
	 * json文件转字符串
	 * @param path
	 * @return
	 */
	public static String jsonFileToString(String path) {
		FileInputStream in = null;
		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[128];
		try {
			in = new FileInputStream(new File(path));
			while(in.read(buf)!=-1){
				sb.append(readBuffer(buf));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	public static String readBuffer(byte[] buffer){
		if(buffer==null)
			return null;
		if(buffer.length==0){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<buffer.length;i++){
			sb.append((char)buffer[i]);		//转换成字符拼接成字符串
		}
		return sb.toString();
	}
	
	/**
	 * 未完善的方法，供测试输出使用
	 * @param map
	 * @return
	 */
	protected static String toString(Map<?, ?> map) {
		if(map==null) {
			return null;
		}
		Iterator<?> iterator = map.keySet().iterator(); 
		StringBuilder mapSb = new StringBuilder();
		while ( iterator.hasNext() ) {
			Object key = iterator.next();   
			mapSb.append(key+":");
			if(map.get(key) instanceof String) {
				mapSb.append(map.get(key).toString()+"\n");
			} else if(map.get(key) instanceof List){
				List l = (List)map.get(key);
				for(int i=0;i<l.size();i++) {
					mapSb.append(l.get(i)+"\n");
				}
			}
		}
		return mapSb.toString();
	}
	
}
