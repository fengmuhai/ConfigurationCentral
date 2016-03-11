package dna.central.zookeeper.client.entity;

import java.util.List;
import java.util.Map;

/** 
* @author fengmuhai
* @date 2016年1月22日 上午11:08:34 
* @version 1.0  
*/
public class Message {

	/**
	 * 消息格式：
	 * 消息跟踪号,消息流水号,{{服务代号,服务地址,消息接收时间,消息响应时间,响应码},...},消息内容
	 */
	private String trackingNo;
	private String serialNo;
	private List<Map<String, String>> serviceRecords;
	private String msgContent;
	
	public Message() {
		
	}

	public String getTrackingNo() {
		return trackingNo;
	}

	public void setTrackingNo(String trackingNo) {
		this.trackingNo = trackingNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public List<Map<String, String>> getServiceRecords() {
		return serviceRecords;
	}

	public void setServiceRecords(List<Map<String, String>> serviceRecords) {
		this.serviceRecords = serviceRecords;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	
	
}
