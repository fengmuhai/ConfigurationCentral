package dna.central.config.entity;


/** 
* @author fengmuhai
* @date 2016年1月21日 下午12:09:27 
* @version 1.0  
*/
public class ZnodePath {

	private String serviceRoot;
	private String serviceCode;
	private String serviceList;
	/*private String serviceUrl;*/
	
	public static void main(String[] args) {
		ZnodePath zp = new ZnodePath("/adsfjkdsf/09092314/DDDFSFGERGRE");
		System.out.println("serviceRoot:"+zp.getServiceRoot());
		System.out.println("serviceCode:"+zp.getServiceCode());
		System.out.println("serviceUrl:"+zp.getServiceList());
	}
	
	public ZnodePath() {
		
	}
	
	@SuppressWarnings("null")
	public ZnodePath(String path) {
		String[] znodes = path.split("/");
		serviceRoot = znodes[1];
		serviceCode = znodes[2];
		serviceList = znodes[3];
		System.out.println(path);
	}

	public String getServiceRoot() {
		return serviceRoot;
	}

	public void setServiceRoot(String serviceRoot) {
		this.serviceRoot = serviceRoot;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceList() {
		return serviceList;
	}

	public void setServiceList(String serviceList) {
		this.serviceList = serviceList;
	}

	
}
