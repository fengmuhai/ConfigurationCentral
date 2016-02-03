package test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.proto.WatcherEvent;

import com.thoughtworks.xstream.XStream;

import dna.central.zookeeper.client.ClientBase;
import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.ZkClientProvider;
import dna.central.zookeeper.client.ZkClientService;
import dna.central.zookeeper.client.entity.RegisterInfo;
import dna.central.zookeeper.client.util.XmlUtil;

/** 
* @author fengmuhai
* @date 2016年1月25日 下午3:53:37 
* @version 1.0  
*/
public class ClientServiceTest implements Watcher{
	
	private ZooKeeper zk;
	private String createrId;
	private boolean contral = false;
	
	public static void main(String[] args){
		
		new ClientServiceTest().testCreate_EPHEMERAL_SEQUENTIAL_node("5");
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//new KeeperWatcher().start();
	}
	
	
	public void testCreate_EPHEMERAL_SEQUENTIAL_node(String createrId) {
		this.createrId = createrId;
		try {
			zk = new ZooKeeper("127.0.0.1:2181",3000,this);
			if(zk.exists(ClientBase.WATCHER_SERVICE_PATH,true) == null) {
				zk.create(ClientBase.WATCHER_SERVICE_PATH, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zk.create(ClientBase.WATCHER_SERVICE_PATH+"/", createrId.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println(zk.getChildren(ClientBase.WATCHER_SERVICE_PATH, true));
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("连接zookeeper成功，创建了临时目录！");
	}

	@Override
	public void process(WatchedEvent event) {
		/*System.out.println("发生了"+event.getType()+"事件，路径为："+event.getPath());
		List<String> list;
		try {
			list = zk.getChildren(ClientBase.WATCHER_SERVICE_PATH, true);
			String[] nodes = list.toArray(new String[list.size()]); 
	        Arrays.sort(nodes);
			if(nodes[0].equals(createrId) && !contral) {
				contral = true;
				System.out.println("我是"+createrId+"号，获得了控制权！");
				zk.getChildren(ClientBase.WATCHER_SERVICE_PATH, true);
			} else if(contral) {
				contral = false;
				System.out.println("我是"+createrId+"号，失去了控制权！");
				zk.getChildren(ClientBase.WATCHER_SERVICE_PATH, true);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
	}
	
}
class KeeperWatcher extends Thread{
	@Override
	public void run() {
		while(true) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
		}
	}
}
