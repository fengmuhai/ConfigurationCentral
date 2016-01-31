package dna.central.zookeeper.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import dna.central.zookeeper.client.entity.ConsumerRegInfo;
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
		
		//开启对数据变化监听
		try {
			zookeeper.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, true, null);
			System.out.println("Begin to Watcher data...");
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
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


