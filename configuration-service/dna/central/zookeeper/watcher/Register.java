package dna.central.zookeeper.watcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.thoughtworks.xstream.XStream;

import dna.central.config.entity.Service;
import dna.central.config.entity.ServiceList;
import dna.central.config.entity.ZnodePath;
import dna.central.zookeeper.client.ClientBase;
import dna.central.zookeeper.client.entity.RegisterInfo;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
* @author fengmuhai
* @date 2016年1月21日 上午10:33:35 
* @version 1.0  
*/
public class Register implements Watcher {

	private static final String changed_node = null;
	private String zookeeperUrl;
	private String serviceCode;
	private String serviceType;
	private String serviceUrl;
	private String configPath;
	
	private static Integer mutex;
	private static ZooKeeper zookeeper = null;
	
	//zookeeper目录的缓存数据
	private static List<String> name_service_nodes;
	
	
	public static void main(String[] args){

		new Register("10.123.65.56:2181", "10.123.65.56:1234","D:/path");
		new ZkWatcher().start();
		//zookeeper.getChildren(C, watch)
	}
	
	public Register() {
	}
	
	public Register(String zookeeperUrl, String serviceUrl, String configPath) {
		try {
			if(zookeeper!=null && zookeeper.getState().isAlive()){
				//zookeeper.close();
			}
			//创建新的连接
			zookeeper = new ZooKeeper(zookeeperUrl, ClientBase.CONNECTION_TIMEOUT, this);
			System.out.println("Zookeeper connection url:"+zookeeperUrl+" successfully!");
			mutex = new Integer(-1);
			

			//设置监视
			//zookeeper.exists(ClientBase.SERVICE_ROOT, true);
			
		} catch (IOException  e) {
			e.printStackTrace();
		}

		//创建新的节点
		createZnode(serviceUrl);
		
		/**
		 * 监听所有节点变化
		 */
		//服务节点的变化
		
		try {
			//配置服务程序开启后，打开对NameService的监听，若有事件发生则会触发process事件
			name_service_nodes = zookeeper.getChildren(ClientBase.SERVICE_ROOT, true);
			for(int i=0;i<name_service_nodes.size();i++) {
				List<String> service_nodes = zookeeper.getChildren(ClientBase.SERVICE_ROOT+"/"+name_service_nodes.get(i), true);
				for(int j=0;j<service_nodes.size();j++) {
					zookeeper.getChildren(ClientBase.SERVICE_ROOT+"/"
							+name_service_nodes.get(i)+"/"+service_nodes.get(j), true);
				}
			}
			
			
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 创建节点，规则参见"dna.central.zookeeper.register/ZkRegister.java 
	 * createZnode(String serviceCode, String serviceUrl)"
	 * @param serviceUrl
	 * @return
	 */
	@SuppressWarnings("finally")
	public  int createZnode(String serviceUrl) {
		int ret = 0;
		try {
			//创建根目录
			if(zookeeper.exists(ClientBase.SERVICE_ROOT, true) == null){
				zookeeper.create(ClientBase.SERVICE_ROOT, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				System.out.println(ClientBase.SERVICE_ROOT+ " registed success!");
			} 
			
			//判断是否有/NameService/serviceCode，如果没有，则创建该路径，用来作为该类服务的根目录
			String path = ClientBase.CONFIGURATION_SERVICE_PATH;
			if(zookeeper.exists(path, true) == null){
				zookeeper.create(path, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				System.out.println(path+ " create success!");
			} else {
				ret = 1;
				System.out.println(path+ " is exsits, can not create again!");
			}
			
			//判断是否有/NameService/ConfigurationService/serviceCode，如果没有，则创建该路径，用来作为该类服务的根目录
			path = ClientBase.CONFIGURATION_SERVICE_PATH+"/"+serviceUrl;
			if(zookeeper.exists(path, true) == null){
				zookeeper.create(path, serviceUrl.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
				System.out.println(serviceUrl+ " registed success!");
			} else {
				ret = 1;
				System.out.println(serviceUrl+ " is exsits, can not regist again!");
			}
			
		} catch (KeeperException e) {
			e.printStackTrace();
			ret = 1;
		} catch (InterruptedException e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			return ret;
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
		String event_type = event.getType().toString();
		String event_path = event.getPath();
		System.out.println("发生"+event.getType()+"事件！\t路径为："+event_path);
		if(event_type.equals(ClientBase.NODE_CHILDEREN_CHANGED)) {  
			//不再废话，肯定重读全部节点数据，set到NameService下
			try {
				ZnodeDataHandler.loadServiceListData();
				System.out.println("更新配置文件成功！目录：/NameService/config_data");
			} catch (KeeperException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			//重新设置监听
			try {
				List<String> new_services_list = zookeeper.getChildren(event_path, true);
				if(event_path.equals(ClientBase.SERVICE_ROOT)) {	//增加了新服务
					new_services_list.retainAll(name_service_nodes);
					String new_service = new_services_list.get(0);
					//对新来的服务节点进行监控
					List<String> service_nodes = zookeeper.getChildren(ClientBase.SERVICE_ROOT+"/"+new_service, true);
					//对新服务的子节点全部进行监控
					watcherServiceNodes(ClientBase.SERVICE_ROOT+"/"+new_service, service_nodes);
				} else {
					ZnodePath zp = new ZnodePath(event_path);
					if(zp.getServiceList().equals("UrlList")) {		//地址列表变化
						List<String> url_list = zookeeper.getChildren(event_path, true);
						for(int i=0;i<url_list.size();i++) {
							zookeeper.exists(event_path+url_list.get(i), true);
						}
					}else if(zp.getServiceList().equals("SubscriberList")) {	//订阅者列表变化
						List<String> subscriber_list = zookeeper.getChildren(event_path, true);
						for(int i=0;i<subscriber_list.size();i++) {
							zookeeper.exists(event_path+subscriber_list.get(i), true);
						}
					}
					
				}
				
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} 
		
		
	}
	
	public static void watcherServiceNodes(String servicePath, List<String> service_nodes) {
		for(int i = 0;i<service_nodes.size();i++) {
			List<String> list = null;
			try {
				list = zookeeper.getChildren(servicePath+"/"+service_nodes.get(i), true);
				for(int j=0;j<list.size();j++) {
					zookeeper.getChildren(servicePath+"/"+service_nodes.get(i)+"/"+list.get(j), true);
				}
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} 
	}
	
	
	
	/**
	 *  public static EventType fromInt(int intValue) {
                switch(intValue) {
                    case -1: return EventType.None;
                    case  1: return EventType.NodeCreated;
                    case  2: return EventType.NodeDeleted;
                    case  3: return EventType.NodeDataChanged;
                    case  4: return EventType.NodeChildrenChanged;

                    default:
                        throw new RuntimeException("Invalid integer value for conversion to EventType");
                }
            }           
	 * @param event
	 */
	
	
	public static void setData(String path, String data, int version) {
		try {
			zookeeper.setData(path, data.getBytes("utf-8"), version);
		} catch (UnsupportedEncodingException 
				 e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	@SuppressWarnings("finally")
	public static byte[] getData(String path) {
		byte data[] = null;
		try {
			 data = zookeeper.getData(path, true, null);
		} catch (KeeperException e) {
			e.printStackTrace();
		} finally {
			return data;
		}
	}
	
	@SuppressWarnings("finally")
	public static String getDataString(String path) {
		byte data[] = null;
		try {
			 data = zookeeper.getData(path, true, null);
		} catch (KeeperException e) {
			e.printStackTrace();
		} finally {
			return new String(data);
		}
	}
	
	/**
	 * 添加新的服务地址
	 * @param new_service
	 * @param path
	 * @param list
	 */
	public static void addServiceUrl(Service new_service, String url, List<Service> list) {
		//在该服务下创建新的服务地址
		new_service.getUrlList().add(url);
		list.add(new_service);
		ServiceList.getServiceList().setServices(list);
		//转化为xml字符串
		String new_config = XmlUtil.toXml(ServiceList.class, XStream.NO_REFERENCES, "url", String.class);
		
		//更新到zookeeper节点
		setData("/", new_config, -1);
	}
	
	public static void deleteServiceUrl() {
		
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

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public static ZooKeeper getZookeeper() {
		return zookeeper;
	}

	public static void setZookeeper(ZooKeeper zookeeper) {
		Register.zookeeper = zookeeper;
	}

	
}

class ZkWatcher extends Thread {
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
