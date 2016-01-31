package dna.central.zookeeper.client.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("providerRegInfo")
public class ProviderRegInfo {

	@XStreamAlias("zookeeperUrl")
	private String zookeeperUrl;
	
	@XStreamAlias("serviceCode")
	private String serviceCode;
	
	@XStreamAlias("serviceType")
	private String serviceType;
	
	@XStreamAlias("responseModel")
	private String responseModel;
	
	@XStreamAlias("describe")
	private String describe;
	
	@XStreamAlias("serviceUrl")
	private String serviceUrl; 
	
	public ProviderRegInfo() {
		
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getResponseModel() {
		return responseModel;
	}

	public void setResponseModel(String responseModel) {
		this.responseModel = responseModel;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
}
