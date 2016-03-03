package dna.central.zookeeper.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import com.thoughtworks.xstream.XStream;

import dna.central.zookeeper.client.entity.RegisterInfo;
import dna.central.zookeeper.client.util.HttpUtil;
import dna.central.zookeeper.client.util.XmlUtil;
import dna.central.zookeeper.watcher.Register;

/** 
* @author fengmuhai
* @date 2016年1月19日 下午5:25:31 
* @version 1.0  
*/
public abstract class ZkClientService {

	private String zookeeperUrl;
	private String serviceCode;
	private String serviceType;
	private String serviceUrl;
	private String configPath;
	
	private static Integer mutex;
	private static ZooKeeper zookeeper = null;
	
	public static void main(String[] args) { 
		String xmlConfig = XmlUtil.xmlFileToString("client-service/regInfo.xml");
		System.out.println(xmlConfig);
		ZkClientService.init(xmlConfig);
	}
	
	public ZkClientService() {
		super();
	}
	
	/**
	 * 构造方法
	 * @param zookeeperUrl	//zookeeper服务器地址，端口号
	 * @param serviceCode	//服务代号
	 * @param registerType 	//注册类型
	 * @param serviceUrl	//服务地址
	 * @param configPath	//服务配置文件路径
	 */
	
	public ZkClientService(String xmlConfig) {
		RegisterInfo regInfo = XmlUtil.toBean(xmlConfig, RegisterInfo.class);
		init(xmlConfig);
	}
	
	/**
	 * 使用xml字符串传参初始化
	 * @param configXml
	 */
	protected static void init(String xmlConfig) {
		RegisterInfo regInfo = (RegisterInfo)XmlUtil.toBean(xmlConfig, RegisterInfo.class);
		String zookeeperUrl = regInfo.getZookeeperUrl();
		final String configPath = regInfo.getConfigPath();
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
					System.out.println("已经触发了" + event.getType() + "事件！"+"\n"+event.getPath()+"\n");
					if(event.getType().toString().equals(ClientBase.NODE_DATA_CHANGED) && 
							event.getPath().equals(ClientBase.SERVICE_CONGIF_DATA_PATH)){
						
						updateConfigToServer(configPath);	//读取节点数据到配置文件
						System.out.println("服务本地更新配置文件成功！");
					}
					synchronized (mutex) {
						mutex.notify();
					}
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Zookeeper connection url:"+zookeeperUrl+" successfully!");

		createZnode(xmlConfig);		//创建节点目录
		//开启对数据变化监听
		try {
			zookeeper.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, true, null);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		new zkWatcher().start();	//执行线程让程序继续运行，实现watcher监视

	}
	
	
	/**
	 * 更新配置文件到本地服务器
	 * @param configPath
	 */
	public static void updateConfigToServer(final String configPath) {
		//读取zookeeper节点更新数据
		byte[] new_data = null;
		try {
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
		
	}
	
	
	/**
	 * 新注册的服务自动创建zookeeper目录节点
	 * 父节点使用服务代号， 子节点使用服务地址命名
	 * @param serviceCode
	 * @param serviceType
	 * @param serviceUrl
	 * @return 0,表示创建成功；-1表示出错； 1表示该命名为注册，已经存在命名服务系统中
	 * 
	 * 创建一个给定的目录节点 path, 并给它设置数据，CreateMode 标识有四种形式的目录节点，分别是 PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；
	 * PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；
	 * EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是 session 超时，这种节点会被自动删除；
	 * EPHEMERAL_SEQUENTIAL：临时自动编号节点
	 */
	@SuppressWarnings("finally")
	public static int createZnode(String xmlConfig) {		//(String serviceCode, String serviceType,String serviceUrl) {
		RegisterInfo regInfo = XmlUtil.toBean(xmlConfig, RegisterInfo.class);
		String serviceCode = regInfo.getServiceCode();
		String serviceType = regInfo.getServiceType();
		String serviceResModel = regInfo.getResponseModel();
		String serviceDescribe = regInfo.getDescribe();
		String serviceUrl = regInfo.getServiceUrl();
		String configPath = regInfo.getConfigPath();
		System.out.println("serviceCode"+serviceCode);
		System.out.println("serviceType"+serviceType);
		System.out.println("serviceResModel"+serviceResModel);
		System.out.println("serviceDescribe"+serviceDescribe);
		System.out.println("serviceUrl"+serviceUrl);
		System.out.println("configPath"+configPath);
		int ret = 0;
		try {
			//创建根目录
			if(zookeeper.exists(ClientBase.SERVICE_ROOT, true) == null){
				zookeeper.create(ClientBase.SERVICE_ROOT, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				System.out.println(ClientBase.SERVICE_ROOT+ " registed success!");
			} 
			//创建配置文件数据目录
			if(zookeeper.exists(ClientBase.SERVICE_CONGIF_DATA_PATH, true) == null){
				zookeeper.create(ClientBase.SERVICE_CONGIF_DATA_PATH, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				System.out.println(ClientBase.SERVICE_CONGIF_DATA_PATH+ " registed success!");
			} 
			
			//判断是否有/NameService/serviceCode，如果没有，则创建该路径，用来作为该类服务的根目录
			String path = ClientBase.SERVICE_ROOT+"/"+serviceCode;
			if(zookeeper.exists(path, true) == null){
				
				zookeeper.create(path, serviceUrl.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path, true);
				
				System.out.println("Service "+serviceCode+ " registed success!");
			} else {
				ret = 1;
				System.out.println("Service "+serviceCode+ " is exsits, can not regist again!");
			}
			
			if(zookeeper.exists(path+ClientBase.SERVICE_CODE_NODE, true)==null){
				zookeeper.create(path+ClientBase.SERVICE_CODE_NODE, 
						serviceCode.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.SERVICE_CODE_NODE, true);
			}
			if(zookeeper.exists(path+ClientBase.SERVICE_TYPE_NODE, true)==null){
				zookeeper.create(path+ClientBase.SERVICE_TYPE_NODE, 
						serviceType.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.SERVICE_TYPE_NODE, true);
			}
			if(zookeeper.exists(path+ClientBase.SERVICE_RESPONSE_MODEL, true)==null) {
				zookeeper.create(path+ClientBase.SERVICE_RESPONSE_MODEL, 
						serviceResModel.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.SERVICE_RESPONSE_MODEL, true);
			}
			if(zookeeper.exists(path+ClientBase.SERVICE_DESCRIBE_NODE, true)==null) {
				zookeeper.create(path+ClientBase.SERVICE_DESCRIBE_NODE, 
						serviceDescribe.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.SERVICE_DESCRIBE_NODE, true);
			}
			if(zookeeper.exists(path+ClientBase.URL_LIST_NODE, true)==null) {
				zookeeper.create(path+ClientBase.URL_LIST_NODE, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.URL_LIST_NODE, true);
			}
			if(zookeeper.exists(path+ClientBase.SUBSCRIBER_LIST_NODE, true)==null) {
				zookeeper.create(path+ClientBase.SUBSCRIBER_LIST_NODE, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
				zookeeper.exists(path+ClientBase.SUBSCRIBER_LIST_NODE, true);
			}
			
			
			
			//判断是否有/NameService/serviceCode/serviceUrl，如果没有，则创建该路径，用来作为服务的目录
			path = ClientBase.SERVICE_ROOT+"/"+serviceCode+ClientBase.URL_LIST_NODE+"/"+serviceUrl;
			if(zookeeper.exists(path, true) == null){
				zookeeper.create(path, serviceUrl.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
				System.out.println(serviceUrl+ " registed success!");
				System.out.println(new String(zookeeper.getData(path, true, null)));
				ret = 0;
			} else {
				ret = 1;
				System.out.println(zookeeper.getChildren(path, false));
				System.out.println(serviceUrl+ " is exsits, can not regist again!");
			}
			
			path = ClientBase.SERVICE_ROOT+"/"+serviceCode+ClientBase.SUBSCRIBER_LIST_NODE+"/"+serviceUrl;
			if(zookeeper.exists(path, true) == null){
				zookeeper.create(path, serviceUrl.getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
				
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
	
	/**
	 * 发送请求消息
	 * @param msg xml字符串类型的消息格式
	 * @return
	 */
	public static String trans(String msg) {
		return HttpUtil.sendPost(msg);
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

	

}

class zkWatcher extends Thread {
	
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


