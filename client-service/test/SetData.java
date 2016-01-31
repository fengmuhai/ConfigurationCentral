package test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class SetData {

	public static void main(String[] args) {
		try {
			ZooKeeper zk = new ZooKeeper("10.123.65.56:2184,10.123.65.56:2181,10.123.65.56:2182",30000,new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					
					
				}
				
			});
		// 修改子目录节点数据
		 zk.setData("/test","set new data successfully!".getBytes(),-1); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
