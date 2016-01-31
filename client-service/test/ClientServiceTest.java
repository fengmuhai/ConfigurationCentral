package test;

import java.io.IOException;

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
public class ClientServiceTest {
	
	public static void main(String[] args){
		
		try {
			ZooKeeper zk = new ZooKeeper("10.123.65.56:2184,10.123.65.56:2181,10.123.65.56:2182",30000,new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					
					
				}
				
			});
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//zk.create("/test", "hello zokeepers!".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			
			/*while(true){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				if(zk.getState().isConnected()) {
					System.out.println(new String(zk.getData("/test",false,null)));
					System.out.println(zk.getSessionId());
					
				} else {
					System.out.println("死了一台zookeeper，重新连接！");
					System.out.println(new String(zk.getData("/test",false,null)));
					zk.create("/test/dead_record", String.valueOf(System.currentTimeMillis()).getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
					zk = new ZooKeeper("10.123.65.56:2181,10.123.65.56:2182,10.123.65.56:2184",30000,new Watcher() {

						@Override
						public void process(WatchedEvent event) {
							
							
						}
						
					});
				}
				
				
				//System.out.println("contected!");
				
			}*/
			
			
			//System.out.println(new String(zk.getData(ClientBase.SERVICE_CONGIF_DATA_PATH, false, null)));
			while(true) {
				//System.out.println(zk.getSessionId());
				
				if(zk.getState().isConnected())
					System.out.print("Zookeeper Server is alive!  ");
				else
					System.out.print("Zookeeper Server is dead!  ");
				
				System.out.print(new String(zk.getData("/test",false,null))+"\t");
				System.out.println(zk.getSessionId());
				
				Thread.sleep(8000);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	
}
