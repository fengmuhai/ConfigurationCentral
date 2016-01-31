package dna.central.zookeeper.watcher;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import dna.central.config.entity.WatcherRegInfo;
import dna.central.config.entity.ZnodePath;
import dna.central.zookeeper.client.ClientBase;
import dna.central.zookeeper.client.util.XmlUtil;

public class WatcherService {
	
	private static ZooKeeper zookeeper;
	
	//zookeeper目录的缓存数据
	private static List<String> name_service_nodes; 
	
	public static void main(String[] args) {
		String xmlRegInfo = XmlUtil.xmlFileToString("configuration-service/watcherRegInfo.xml");
		System.out.println("conXmlInfo:"+xmlRegInfo);
		WatcherService.init(xmlRegInfo );
	}
	
	public WatcherService() {
		
	}
	
	public static void init(String xmlRegInfo) {
		if(xmlRegInfo==null || xmlRegInfo.equals("")) {
			System.out.println("xmlRegInfo is null, register failed!");
			return;
		}
		
		WatcherRegInfo watcherRegInfo = XmlUtil.toBean(xmlRegInfo, WatcherRegInfo.class);
		String zookeeperUrl = watcherRegInfo.getZookeeperUrl();
		System.out.println("zookeeperUrl:"+zookeeperUrl);
		try {
			zookeeper = new ZooKeeper(zookeeperUrl, ClientBase.CONNECTION_TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					System.out.println("触发了"+event.getType()+"事件！路径为："+event.getPath());
					eventExecuteHandler(event);
					
				}
				
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Zookeeper 监控程序启动，一旦zookeeper节点数据变化，就会触发重新读取数据操作...");
		
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
		
		//保持监听
		new ZookeeperWatcher().start();
		
	}

	/**
	 * 事件处理逻辑
	 * @param event
	 */
	protected static void eventExecuteHandler(WatchedEvent event) {
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

	private static void watcherServiceNodes(String servicePath, List<String> service_nodes) {
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

	public static ZooKeeper getZookeeper() {
		
		return zookeeper;
	}

}
class ZookeeperWatcher extends Thread {
	
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


