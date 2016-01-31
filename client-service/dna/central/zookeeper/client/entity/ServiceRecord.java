package dna.central.zookeeper.client.entity;
/** 
* @author fengmuhai
* @date 2016年1月22日 上午11:31:12 
* @version 1.0  
*/
//某次请求服务的记录，放在Message中
public class ServiceRecord {

	/**
	 * {服务代号，服务地址，消息接收时间，消息响应时间}
	 */
	private String serviceCode;
	private String serviceUrl;
	private String recivedTime;
	private String responseTime;
	
	public ServiceRecord() {
		
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getRecivedTime() {
		return recivedTime;
	}

	public void setRecivedTime(String recivedTime) {
		this.recivedTime = recivedTime;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
}
