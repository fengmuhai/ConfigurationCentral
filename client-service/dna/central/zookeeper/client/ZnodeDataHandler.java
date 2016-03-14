package dna.central.zookeeper.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;


import dna.central.zookeeper.client.ClientBase;
import dna.central.zookeeper.client.entity.Service;
import dna.central.zookeeper.client.entity.ServiceList;

/** 
* @author fengmuhai
* @date 2016年1月25日 上午11:22:19 
* @version 1.0  
*/
public class ZnodeDataHandler {

	private static ZooKeeper zk = ZkClientConsumer.getZookeeper();
	
	public static void main(String[] args) {
		try {
			zk = new ZooKeeper("10.123.65.56:2181",3000,new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					System.out.println("=====已经触发了" + event.getType() +"的"+event.getPath()+ "事件！"+"\n"+event.getState());
					
				}
				
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			loadServiceListData();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static ServiceList loadServiceListData() throws KeeperException, InterruptedException {
		List<String> serviceZnodes = getChildren(ClientBase.SERVICE_ROOT);		//获取/NameService路径下的全部节点
		ServiceList service_list = ServiceList.getServiceList();
		List<Service> services = new ArrayList<Service>();
		for(int i=0;i<serviceZnodes.size();i++){
			boolean isService = false;
			String code = null;
			String type = null;
			String describe = null;
			String response_model = null;
			List<String> url_list = new ArrayList<String>();
			List<String> subscriber_list = new ArrayList<String>();
			
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_CODE_NODE, false) != null){
				code = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_CODE_NODE);
			}
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_TYPE_NODE, false) != null){
				type = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_TYPE_NODE);
			}
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_RESPONSE_MODEL, false) != null){
				response_model = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_RESPONSE_MODEL);
			}
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_DESCRIBE_NODE, false) != null){
				describe = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.SERVICE_DESCRIBE_NODE);
			}
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.URL_LIST_NODE, false) != null){
				List<String> urlListZnodes = getChildren(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.URL_LIST_NODE);
				for(int j=0;j<urlListZnodes.size();j++){
					String urlData = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i) +ClientBase.URL_LIST_NODE+ "/"+urlListZnodes.get(j));
					url_list.add(urlData);
				}
				isService = true;
			}
			if(zk.exists(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i)+ClientBase.URL_LIST_NODE, false) != null){
				List<String> subscriberListZnodes = getChildren(ClientBase.SERVICE_ROOT +"/"+serviceZnodes.get(i)+ ClientBase.SUBSCRIBER_LIST_NODE);
				
				for(int j=0;j<subscriberListZnodes.size();j++){
					String subscriberData = getZnodeData(ClientBase.SERVICE_ROOT + "/"+serviceZnodes.get(i) + ClientBase.SUBSCRIBER_LIST_NODE + "/"+subscriberListZnodes.get(j));
					subscriber_list.add(subscriberData);
				}
			}
			
			if(isService){
				
				Service service = new Service(code, type, response_model, describe, url_list, subscriber_list);
				services.add(service);
				service_list.setServices(services);
				//System.out.println(service.toString());
				
			}
			
			
		}
		//String serviceListXml = XmlUtil.toXml(service_list, XStream.NO_REFERENCES, "url", String.class);
		//System.out.println("serviceListXml"+serviceListXml);

		return service_list;
	}
	
	/**
	 * 获取指定路径下的子节点目录
	 * @param path
	 * @return
	 */
	public static List<String> getChildren(String path) {
		List<String> znodes = null;
		try {
			znodes = zk.getChildren(path, true);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return znodes;
	}
	
	/**
	 * 获取指定路径下的数据
	 * @param path
	 * @return
	 */
	public static String getZnodeData(String path) {
		try {
			return new String(zk.getData(path, false, null));
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}


