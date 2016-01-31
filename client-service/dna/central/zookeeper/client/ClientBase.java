package dna.central.zookeeper.client;
/** 
* @author fengmuhai
* @date 2016年1月19日 下午5:42:51 
* @version 1.0  
*/
public class ClientBase {

	public static final int CONNECTION_TIMEOUT = 3000;			//连接ZooKeeper超时时间
	public static final String SERVICE_ROOT = "/NameService";	//服务注册的根目录
	public static final String SERVICE_CONGIF_DATA_PATH = SERVICE_ROOT+"/config_data";				//配置文件节点
	public static final String CONFIGURATION_SERVICE_PATH = SERVICE_ROOT+"/ConfigurationService";	//配置服务目录	

	public static final String NODE_DATA_CHANGED = "NodeDataChanged";	//watcher触发事件名
	public static final String NODE_CREATED = "NodeCreated";
	public static final String NODE_DELETED = "NodeDeleted";
	public static final String NODE_CHILDEREN_CHANGED = "NodeChildrenChanged";
	public static final String NONE = "None";
	
	/**
	 * 服务目录下的子目录定义
	 */
	public static final String SERVICE_CODE_NODE = "/Code";
	public static final String SERVICE_TYPE_NODE = "/Type";
	public static final String SERVICE_RESPONSE_MODEL = "/ResponseModel";
	public static final String SERVICE_DESCRIBE_NODE = "/Describe";
	public static final String URL_LIST_NODE = "/UrlList";
	public static final String SUBSCRIBER_LIST_NODE = "/SubscriberList";
	
}
