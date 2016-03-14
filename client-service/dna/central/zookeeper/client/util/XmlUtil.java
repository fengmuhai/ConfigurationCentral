package dna.central.zookeeper.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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
	public static <T> T toBean(String xmlStr, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(cls);
        @SuppressWarnings("unchecked")
        T t = (T) xstream.fromXML(xmlStr);
        return t;
    }
	

	/**
	 * xml文件转对象
	 * @param xmlFile	xml文件
	 * @param cls		要转成的类
	 * @return			该类的实体对象
	 */
	public static <T> T toBean(File xmlFile, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		@SuppressWarnings("unchecked")
		T t = (T) xstream.fromXML(xmlFile);
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
	
	/**
	 * xml文件转xml字符串
	 * @param path
	 * @return
	 */
	public static String xmlFileToString(String path) {
		FileReader reader = null;
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[1024];
		try {
			reader = new FileReader(path);
			int temp = 0;
			while((temp=reader.read(buf))!=-1){
				sb.append(String.valueOf(buf,0,temp));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	
}

