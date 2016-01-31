package dna.central.zookeeper.test;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import dna.central.zookeeper.client.util.XmlUtil;
import dna.central.zookeeper.watcher.WatcherService;

public class ZookeeperWatcherTest {

	public static void main(String[] args) {

		/*ZooKeeper zk = null;
		try {
			zk = new ZooKeeper("10.123.65.56:2184,10.123.65.56:2181,10.123.65.56:2182",30000,new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					
					
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		int i=0;
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
			setDataTest(zk, "data_change====="+i+"=====times!!");
			System.out.println("数据写入成功！！第"+i+"=====times!!");
		}*/
		
		//watcherServiceTest();
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper("10.123.65.56:2184,10.123.65.56:2181,10.123.65.56:2182",30000,new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					
					
				}
				
			});
			
			System.out.println(zk.getChildren("/NameService", false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDataTest(ZooKeeper zk, String new_data) {
		try {
			zk.setData("/test", new_data.getBytes(), -1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void watcherServiceTest() {
		String xmlRegInfo = XmlUtil.xmlFileToString("configuration-service/watcherRegInfo.xml");
		System.out.println("conXmlInfo:"+xmlRegInfo);
		WatcherService.init(xmlRegInfo );
	}
	
}
