package dna.central.zookeeper.watcher;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import dna.central.config.entity.WatcherRegInfo;
import dna.central.config.entity.ZnodePath;
import dna.central.zookeeper.client.ClientBase;
import dna.central.zookeeper.client.util.XmlUtil;

public class WatcherService {
	
	private static ZooKeeper zookeeper;
	
	
	private static List<String> name_service_nodes;		//zookeeper服务目录的缓存数据
	private static String[] nodes_sequence_array;		//watcher服务的节点顺序
	private static String my_node;
	
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
					//System.out.println("触发了"+event.getType()+"事件！路径为："+event.getPath());
					eventExecuteHandler(event);
					
				}
				
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//尝试创建在目录"/WatcherService"下创建节点来获取监控权，监控权由最小编号的watcher获得
		try {
			if(zookeeper.exists(ClientBase.WATCHER_SERVICE_PATH, true) == null) {
				zookeeper.create(ClientBase.WATCHER_SERVICE_PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
				
			//创建“临时的自动编号”节点
			zookeeper.create(ClientBase.WATCHER_SERVICE_PATH+"/", "".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			/**
			 * 开启对"/WatcherService"全部子节点的监控;
			 * 新来的节点不需要监控，因为在自动编号创建模式下，新来的watcher服务节点序号肯定比当前服务节点序号要大
			 */
			List<String> list = zookeeper.getChildren(ClientBase.WATCHER_SERVICE_PATH, false);	//不监控节点ChildrenChanged事件
			for(int i=0;i<list.size();i++) {
				zookeeper.exists(ClientBase.WATCHER_SERVICE_PATH+"/"+list.get(i), true);
			}
			//获取当前"/WatcherService"目录下的子节点的排序
			updateWatcherNodesSequence();
			System.out.println("获取Watcher服务列表成功！列表为：");
			for(int i=0;i<list.size();i++){
				System.out.print(nodes_sequence_array[i]+"\t");
			}
			//记录自己的节点编号
			my_node = nodes_sequence_array[nodes_sequence_array.length-1];
			System.err.println("我是"+my_node+"号Watcher服务！");
			
		} catch (KeeperException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
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
		

		System.out.println("Zookeeper 监控程序启动，一旦zookeeper节点数据变化，就会触发重新读取数据操作...");
		
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
		if(event_type.equals(ClientBase.NODE_CHILDEREN_CHANGED)) {  //服务节点的变化  
			reWatcherServiceNodes(event_path);
			if(!WatcherService.getWatcherLock()){	//实现了多服务间的同步锁
				return;
			}
			//不再废话，肯定重读全部节点数据，set到NameService下
			try {
				ZnodeDataHandler.loadServiceListData();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("更新配置文件成功！目录：/NameService/config_data");
			
		} else if(event_type.equals(ClientBase.NODE_DELETED) && 
				event_path.contains(ClientBase.WATCHER_SERVICE_PATH)) {	//监听服务的NodeDelete事件
			
			updateWatcherNodesSequence();	//重新对watcher节点列表排序，更新到缓存
			
		}
		
	}
	
	/**
	 * 根据监听到的ChildrenNodeChange事件，定位到具体位置，重新设置服务的监听
	 * @param event_path
	 */
	private static void reWatcherServiceNodes(String event_path) {
		//重新设置监听
		try {
			List<String> new_services_list = zookeeper.getChildren(event_path, true);
			if(event_path.equals(ClientBase.SERVICE_ROOT)) {	//增加了新服务
				new_services_list.retainAll(name_service_nodes);	//过滤掉旧的服务
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
	
	/**
	 * 通过是否是最小的编号服务来决定是否获得锁(监视权)
	 * @return
	 */
	private static boolean getWatcherLock() {
		if(my_node.equals(nodes_sequence_array[0])) {
			return true;
		}
		return false;
	}

	/**
	 * 重新对watcher节点列表排序，更新到缓存
	 */
	private static void updateWatcherNodesSequence() {
		try {
			List<String> list = zookeeper.getChildren(ClientBase.WATCHER_SERVICE_PATH, false);
			nodes_sequence_array = list.toArray(new String[list.size()]);
			Arrays.sort(nodes_sequence_array);
			System.out.println("更新Watcher服务列表成功！列表为：");
			for(int i=0;i<list.size();i++){
				System.out.print(nodes_sequence_array[i]+"\t");
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 监控服务节点
	 * @param servicePath
	 * @param service_nodes
	 */
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


