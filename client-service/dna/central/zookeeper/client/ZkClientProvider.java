package dna.central.zookeeper.client;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import dna.central.zookeeper.client.entity.ProviderRegInfo;
import dna.central.zookeeper.client.util.XmlUtil;

public class ZkClientProvider {

	private static ProviderRegInfo providerRegInfo;
	private static ZooKeeper zookeeper;
	
	public static void main(String[] args){

		String regXmlInfo = XmlUtil.xmlFileToString("client-service/providerRegInfo.xml");
		System.out.println(regXmlInfo);
		ZkClientProvider.init(regXmlInfo);

	}
	
	
	public ZkClientProvider() {
		
	}
	
	/**
	 * 使用xml字符串传参初始化
	 * @param configXml
	 */
	public static void init(String regXmlInfo) {
		if(regXmlInfo==null || regXmlInfo.equals("")) {
			System.out.println("regXmlInfo is null, register failed!");
			return;
		}
		providerRegInfo = XmlUtil.toBean(regXmlInfo, ProviderRegInfo.class);
		String zookeeperUrl = providerRegInfo.getZookeeperUrl();
		
		if(zookeeper!=null && zookeeper.getState().isAlive()){
			try {
				zookeeper.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {	//创建新的连接
			zookeeper = new ZooKeeper(zookeeperUrl, ClientBase.CONNECTION_TIMEOUT,null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Provider connected url:"+zookeeperUrl+" successfully!");
		
		//不需要开启对数据变化监听
		
		//创建目录
		createZnode(regXmlInfo);
		System.out.println("Provider create successfully!");
		
		//保持连接
		new ProviderKeeper().start();
		
	
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
	protected static int createZnode(String regXmlInfo) {		//(String serviceCode, String serviceType,String serviceUrl) {
		ProviderRegInfo regInfo = XmlUtil.toBean(regXmlInfo, ProviderRegInfo.class);
		String serviceCode = regInfo.getServiceCode();
		String serviceType = regInfo.getServiceType();
		String serviceResModel = regInfo.getResponseModel();
		String serviceDescribe = regInfo.getDescribe();
		String serviceUrl = regInfo.getServiceUrl();
		/*System.out.println("serviceCode"+serviceCode);
		System.out.println("serviceType"+serviceType);
		System.out.println("serviceResModel"+serviceResModel);
		System.out.println("serviceDescribe"+serviceDescribe);
		System.out.println("serviceUrl"+serviceUrl);
		System.out.println("configPath"+configPath);*/
		int ret = 0;
		try {
			//创建根目录
			if(zookeeper.exists(ClientBase.SERVICE_ROOT, false) == null){
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
	
	
}

class ProviderKeeper extends Thread {
	
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


