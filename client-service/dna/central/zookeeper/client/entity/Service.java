package dna.central.zookeeper.client.entity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import dna.central.zookeeper.client.entity.RegisterInfo;

/** 
* @author fengmuhai
* @date 2016年1月19日 上午11:24:23 
* @version 1.0  
*/
@XStreamAlias("service")
public class Service {

	/**
	 * 服务列表对象:
	 * 服务代号，服务类型，响应模式，服务说明，服务地址列表（负载，分组），服务订阅者
	 */
	@XStreamAlias("code")
	//@XStreamAsAttribute	//此注解的xml文件会将该属性标签放在类的标签中
	private String code;
	@XStreamAlias("type")
	private String type;
	@XStreamAlias("responseType")
	private String responseType;
	@XStreamAlias("dsicribe")
	private String describe;
	@XStreamAlias("urlList")
	private List<String> urlList;
	@XStreamAlias("subscribers")
	private List<String> subscribers;
	
	/**
	 * 无参构造函数
	 */
	public Service() {
		
	}
	
	/**
	 * 初始化全部参数构造函数
	 * @param code
	 * @param type
	 * @param responseType
	 * @param describe
	 * @param urlList
	 * @param subscribers
	 */
	public Service(String code, String type, String responseType, String describe, List<String> urlList,
			List<String> subscribers) {
		super();
		this.code = code;
		this.type = type;
		this.responseType = responseType;
		this.describe = describe;
		this.urlList = urlList;
		this.subscribers = subscribers;
	}
	
	/**
	 * 功能类似于构造函数的方法，只是不需要new就可以改变对象值
	 * @param code
	 * @param type
	 * @param responseType
	 * @param describe
	 * @param urlList
	 * @param subscribers
	 */
	public void setService(String code, String type, String responseType, String describe, List<String> urlList,
			List<String> subscribers) {
		this.code = code;
		this.type = type;
		this.responseType = responseType;
		this.describe = describe;
		this.urlList = urlList;
		this.subscribers = subscribers;
	}
	
	@Override
	public String toString() {
		return "code:"+code+"\ntype:"+type+"\nresponseType:"+responseType+
				"\ndescribe:"+describe+"\nurlList:"+urlList+"\nsubscribers:"+subscribers;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public List<String> getUrlList() {
		return urlList;
	}
	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}
	public List<String> getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(List<String> subscribers) {
		this.subscribers = subscribers;
	}
	
	
	
}
