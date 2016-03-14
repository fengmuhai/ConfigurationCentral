package dna.central.zookeeper.client.entity;

import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/** 
* @author  作者 E-mail: fmuhai@foxmail.com
* @date 创建时间：2016年1月23日 下午4:49:58 
* @version 1.0 
*/
//服务地址列表
@XStreamAlias("serviceList")
public class ServiceList {

	@XStreamAlias("services")
	@XStreamAsAttribute
	private List<Service> services;
	
	//单利模式
	private static ServiceList serviceList = new ServiceList();
	private ServiceList() {}
	public static ServiceList getServiceList() {
		return serviceList;
	}
	
	
	public boolean contains(Object obj) {
		for(int i=0;i<this.services.size();i++){
			if(this.services.get(i).equals(obj)){
				return true;
			}
		}

		return false;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	/**
	 * 通过服务代号获取服务
	 * @param code
	 * @return
	 */
	public Service getServiceByCode(String code) {
		for(int i=0; i<services.size();i++){
			if(services.get(i).getCode().equals(code)) {
				return services.get(i);
			}
		}
		return null;
	}
}
