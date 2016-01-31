package dna.central.zookeeper.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/** 
* @author fengmuhai
* @date 2016年1月22日 上午10:22:22 
* @version 1.0  
*/
public class XmlUtil {
	
	public static void main(String[] args) {
		
	}

	/**
	 * xml字符串转对象
	 * @param xmlStr
	 * @param cls
	 * @return
	 */
	/*public static <T> T toBean(String xmlStr, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		@SuppressWarnings("unchecked")
		T t = (T) xstream.toXML(xmlStr);
		return t;
		
	}*/
	
	public static <T> T toBean(String xmlStr, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        @SuppressWarnings("unchecked")
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }
	
	public static String toXml(Object obj) {
		XStream xstream = new XStream(new DomDriver("utf8"));
        xstream.processAnnotations(obj.getClass()); // 识别obj类中的注解
        /*
         // 以压缩的方式输出XML
         StringWriter sw = new StringWriter();
         xstream.marshal(obj, new CompactWriter(sw));
         return sw.toString();
         */
        // 以格式化的方式输出XML
        return xstream.toXML(obj);
	}
	
	/**
	 * 将类中的列表对象转化为xml字符串，指定reference属性
	 * @param obj  XStream.NO_REFERENCES
	 * @param mode
	 * @return
	 */
	public static String toXml(Object obj, int mode) {
		XStream xstream = new XStream(new DomDriver("utf-8"));
		xstream.processAnnotations(obj.getClass());		//识别obj泪的注解，不加这行代码会用包名和类名作为标签
		xstream.setMode(mode);		//设置reference模型
		return xstream.toXML(obj);
	}
	
	/**
	 * 将类中的列表对象转化为xml字符串，指定reference属性
	 * 给类中某个属性（List，Set）装载的类设置新的标签
	 * @param <T>
	 * @param obj  XStream.NO_REFERENCES
	 * @param mode
	 * @return
	 */
	public static <T> String toXml(Object obj, int mode, String new_label, Class<T> className) {
		XStream xstream = new XStream(new DomDriver("utf-8"));
		xstream.processAnnotations(obj.getClass());		//识别obj泪的注解，不加这行代码会用包名和类名作为标签
		xstream.setMode(mode);		//设置reference模型
		xstream.alias(new_label, className);	//修改标签
		return xstream.toXML(obj);
	}
	
	
	public static String xmlFileToString(String path) {
		FileInputStream in = null;
		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[1];
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
	

	
}

