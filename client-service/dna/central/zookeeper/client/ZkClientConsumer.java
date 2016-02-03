package dna.central.zookeeper.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import dna.central.http.client_util.HttpClient;
import dna.central.zookeeper.client.entity.ConsumerRegInfo;
import dna.central.zookeeper.client.entity.Message;
import dna.central.zookeeper.client.entity.ServiceRecord;
import dna.central.zookeeper.client.util.HttpUtil;
import dna.central.zookeeper.client.util.JacksonTools;
import dna.central.zookeeper.client.util.XmlUtil;

public class ZkClientConsumer implements Watcher {
	
	private static ConsumerRegInfo consumerRegInfo;
	private static ZooKeeper zookeeper;
	
	public static void main(String[] args) {
		String conXmlInfo = XmlUtil.xmlFileToString("client-service/consumerRegInfo.xml");
		ZkClientConsumer.init(conXmlInfo);
		
	}
	

	public ZkClientConsumer() {
		
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
					System.out.println("已经触发了" + event.getType() + "事件！"+"\n"+event.getPath()+"\n");
					if(event.getType().toString().equals(ClientBase.NODE_DATA_CHANGED) && 
							event.getPath().equals(ClientBase.SERVICE_CONGIF_DATA_PATH)){
						
						updateConfigToServer(consumerRegInfo.getConfigPath());	//读取节点数据到配置文件
						System.out.println("服务本地更新配置文件成功！");
					}
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//zookeeper.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, true, null);
		//获取最新节点数据到配置文件,并开启对数据节点监听(获取时会重新设置监听)
		updateConfigToServer(consumerRegInfo.getConfigPath());	
		
		System.out.println("Begin to Watcher data...");
		
		new ConsumerWatcher().start();	//执行线程让程序继续运行，实现watcher监视
		
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("已经触发了" + event.getType() + "事件！"+"\n"+event.getPath()+"\n");
		if(event.getType().toString().equals(ClientBase.NODE_DATA_CHANGED) && 
				event.getPath().equals(ClientBase.SERVICE_CONGIF_DATA_PATH)){
			
			updateConfigToServer(consumerRegInfo.getConfigPath());	//读取节点数据到配置文件,读取时会重新设置监听
			System.out.println("服务本地更新配置文件成功！");
		}
	}
	
	
	/**
	 * 更新配置文件到本地服务器
	 * @param configPath
	 */
	protected static void updateConfigToServer(final String configPath) {
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
		
	}
	
	/**
	 * 发送请求消息
	 * @param msg xml字符串类型的消息格式
	 * @return
	 */
	public static String trans(String msg) {
		return HttpUtil.sendPost(msg);
	}
	
	/**
	 * 调用服务请求
	 * @param msg
	 * @return
	 */
	public static String call(String jsonMsg) {
		/*Map<?, ?> map = JacksonTools.jsonStr2Map(jsonMsg);
		List records = (List)map.get("service-records");
		@SuppressWarnings("unchecked")
		Map<String, String> maps = (Map<String, String>)records.get(records.size()-1);
		String url = (String) maps.get("service-url");*/
		
		Message msg = JacksonTools.json2Object(jsonMsg, Message.class);
		List<ServiceRecord> serviceRecords = msg.getServiceRecords();
		//List是有序的取得最后一个记录即可获得最新请求的URL
		String url = serviceRecords.get(serviceRecords.size()-1).getServiceUrl();
		
		HttpClient hc = new HttpClient("POST", url, "UTF-8", 0, 0, jsonMsg.getBytes(), null);
		return new String((byte[]) hc.send().getDataValue());
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


