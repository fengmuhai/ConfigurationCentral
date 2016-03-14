package dna.central.zookeeper.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.thoughtworks.xstream.XStream;

import dna.central.httpClient.HttpClient;
import dna.central.zookeeper.client.entity.ConsumerRegInfo;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.entity.Service;
import dna.central.zookeeper.client.entity.ServiceList;
import dna.central.zookeeper.client.entity.ZnodePath;
import dna.central.zookeeper.client.util.JacksonTools;
import dna.central.zookeeper.client.util.MessageUtils;
import dna.central.zookeeper.client.util.XmlUtil;

public class ZkClientConsumer {
	
	
	private static ConsumerRegInfo consumerRegInfo;
	private static ZooKeeper zookeeper;
	
	private static List<String> name_service_nodes;		//zookeeper服务目录的缓存数据

	private static Logger logger = Logger.getLogger("client-service.dna.central.zookeeper.client.ZkClientConsumer");
	private static ServiceList serviceList;				//服务列表缓存
	

	public ZkClientConsumer() {
		
	}
	
	/**
	 * 使用xml字符串传参初始化
	 * @param configXml
	 */
	public static void init(String regXmlInfo) {
		if(regXmlInfo==null || regXmlInfo.equals("")) {
			logger.warning("regXmlInfo is null, register failed!");
			return;
		}
		consumerRegInfo = XmlUtil.toBean(regXmlInfo, ConsumerRegInfo.class);
		String zookeeperUrl = consumerRegInfo.getZookeeperUrl();
		
		if(zookeeper!=null && zookeeper.getState().isAlive()){
			try {
				zookeeper.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {	//创建新的连接
			zookeeper = new ZooKeeper(zookeeperUrl, ClientBase.CONNECTION_TIMEOUT,new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					logger.info("已经触发了" + event.getType() + "事件！"+"\n节点路径为："+event.getPath()+"\n");
					//watchAllNodesChildren();		//一旦事件触发，也可以重新watch全部节点
					eventExecuteHandler(event);
					
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//zookeeper.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, true, null);
		//获取最新节点数据到到缓存和配置文件,并开启对数据节点监听(获取时会重新设置监听)
		try {
			ServiceList services = ZnodeDataHandler.loadServiceListData(); 
			setServiceList(services);
			logger.info("服务列表更新到本地缓存成功！！");
			loadServiceList(consumerRegInfo.getConfigPath(), services);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
		watchAllNodesChildren();
		logger.info("Begin to Watch nodes...");
		
		new ConsumerWatcher().start();	//执行线程让程序继续运行，实现watcher监视
		
	}
	
	/**
	 * 注册时，监听所有服务节点变化
	 */
	protected static void watchAllNodesChildren() {
		try {
			//配置服务程序开启后，打开对NameService的监听，若有事件发生则会触发process事件
			name_service_nodes = zookeeper.getChildren(ClientBase.SERVICE_ROOT, true);
			for(int i=0;i<name_service_nodes.size();i++) {
				//开机时初始化服务轮询记录
				ClientBase.ROUND_ROBIN_RECORD.put(name_service_nodes.get(i), 0);
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
		logger.info("Zookeeper 监控程序启动，一旦zookeeper节点数据变化，就会触发重新读取数据操作...");
	}
	

	/**
	 * 事件处理逻辑
	 * @param event
	 */
	protected static void eventExecuteHandler(WatchedEvent event) {
		
		String event_type = event.getType().toString();
		String event_path = event.getPath();
		if(event_type.equals(ClientBase.NODE_CHILDEREN_CHANGED)) {  //服务节点的变化  
			reWatcherServiceNodes(event_path);
			//不再废话，肯定重读全部节点数据，更新本地缓存
			try {
				setServiceList(ZnodeDataHandler.loadServiceListData());
				loadServiceList(consumerRegInfo.getConfigPath(), serviceList);
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("更新服务配置文件成功！");
			
		} else if(event_type.equals(ClientBase.NODE_DELETED) && 
				event_path.contains(ClientBase.WATCHER_SERVICE_PATH)) {	//监听服务的NodeDelete事件
			
			//updateWatcherNodesSequence();	//重新对watcher节点列表排序，更新到缓存
			logger.warning("还未对该事件还未进相应处理！");
			
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
				new_services_list.removeAll(name_service_nodes);	//过滤掉旧的服务
				String new_service = new_services_list.get(0);
				//对新来的服务节点进行监控
				List<String> service_nodes = zookeeper.getChildren(ClientBase.SERVICE_ROOT+"/"+new_service, true);
				//对新服务的子节点全部进行监控
				watcherServiceNodes(ClientBase.SERVICE_ROOT+"/"+new_service, service_nodes);
				//初始化新服务的轮询记录
				ClientBase.getROUND_ROBIN_RECORD().put(new_service, 0);
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
	
	/**
	 * 更新配置文件到本地服务器
	 * @param configPath
	 */
	protected static void loadServiceList(final String configPath, ServiceList serviceList) {
		String serviceListXml;
		serviceListXml = XmlUtil.toXml(serviceList, XStream.NO_REFERENCES, "url", String.class);
		//System.out.print(serviceListXml);
		
		//写入配置文件
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(configPath);		//文件不存在会自动创建
			out.write(serviceListXml.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 调用服务请求
	 * @param msg
	 * @return
	 */
	public static Message call(Message msg) {
		
		String jsonMsg = JacksonTools.object2Json(msg);
		if(jsonMsg==null || jsonMsg.equals("")) {
			System.err.println("jsonMsg is null!");
			return null;
		}
		String url = getUrlByRemoteMode(msg,ClientBase.ROUND_ROBIN);	//随机调用
		HttpClient hc = new HttpClient("POST", url, "UTF-8", 0, 0, jsonMsg.getBytes(), null);
		String resp_str = null;
		try {
			resp_str = new String((byte[]) (hc.send().getDataValue()));
		} catch (Exception e) {
			if(e instanceof java.lang.NullPointerException) {
				//服务器处理失败
				MessageUtils.setResponseCode(msg, "001");
				resp_str = MessageUtils.toJsonStr(msg);
			}
		}
		hc = null;
		return MessageUtils.toMessage(resp_str);
		
	}
	
	public static String getUrlByRemoteMode(Message message, int mode) {
		
		//List是有序的取得最后一个记录即可获得最新请求的服务代号
		String code = MessageUtils.getLatelyRecord(message).get("serviceCode");
		Service service = serviceList.getServiceByCode(code);
		List<String> urlList = service.getUrlList();
		if(urlList.size()<=0){
			logger.warning("该服务代号对应的服务无地址列表，请确认服务代号无误！");
			return null;
		}
		String url = "";
		switch(mode){
			case ClientBase.RANDOM: 
				url = urlList.get((int)(Math.random()*urlList.size()));
			case ClientBase.ROUND_ROBIN:
				url = urlList.get(ClientBase.getRoundRobinChance(code)%2);
			case ClientBase.WEIGHT_BASED:
				url = urlList.get((int)(Math.random()*urlList.size()));
		}
		return "http://"+url;
	}

	
	
	/* 程序使用了静态方法初始化，process方法写在了new Watcher(){}中
	 * @Override
		public void process(WatchedEvent event) {
			System.out.println("已经触发了" + event.getType() + "事件！"+"\n"+event.getPath()+"\n");
			if(event.getType().toString().equals(ClientBase.NODE_DATA_CHANGED) && 
					event.getPath().equals(ClientBase.SERVICE_CONGIF_DATA_PATH)){
				updateConfigToServer(consumerRegInfo.getConfigPath());	//读取节点数据到配置文件,读取时会重新设置监听
				System.out.println("服务本地更新配置文件成功！");
			}
		}
	*/
	
	
	/**
	 * 更新配置文件到本地服务器
	 * @param configPath
	 */
	/*protected static void updateConfigToServer(final String configPath) {
		//读取zookeeper节点更新数据
		byte[] new_data = null;
		try {
			//获取数据并重新设置监听
			new_data = zookeeper.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, true, null);
			
		} catch (KeeperException  e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		//写入配置文件
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(configPath);		//文件不存在会自动创建
			out.write(new_data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}*/
	
	

	/*@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}*/


	public static ServiceList getServiceList() {
		return serviceList;
	}


	public static void setServiceList(ServiceList serviceList) {
		ZkClientConsumer.serviceList = serviceList;
	}


	public static ZooKeeper getZookeeper() {
		return zookeeper;
	}
	
	
}

class ConsumerWatcher extends Thread {
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}


