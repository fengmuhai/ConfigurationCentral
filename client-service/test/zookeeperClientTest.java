package test;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import dna.central.zookeeper.client.ClientBase;

/** 
* @author  作者 E-mail: fmuhai@foxmail.com
* @date 创建时间：2016年1月24日 下午10:24:01 
* @version 1.0 
*/
public class zookeeperClientTest implements Watcher{

	public static void main(String[] args){
		new zookeeperClientTest().register();
	}
	
	public void register(){
		try {
			// 创建一个与服务器的连接
			 ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 30000, this); 
			 // 创建一个目录节点
			 zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
			   CreateMode.PERSISTENT); 
			 Thread.sleep(1000);
			 // 创建一个子目录节点
			 zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
			   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
			 Thread.sleep(1000);
			 System.out.println(new String(zk.getData("/testRootPath",false,null))); 
			 // 取出子目录节点列表
			 System.out.println(zk.getChildren("/testRootPath",true)); 
			 // 修改子目录节点数据
			 zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
			 Thread.sleep(1000);
			 System.out.println("目录节点状态：["+zk.exists("/testRootPath",true)+"]"); 
			 // 创建另外一个子目录节点
			 zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), 
			   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
			 Thread.sleep(1000);
			 System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
			 // 删除子目录节点
			 zk.delete("/testRootPath/testChildPathTwo",-1); 
			 Thread.sleep(1000);
			 zk.delete("/testRootPath/testChildPathOne",-1); 
			 Thread.sleep(1000);
			 // 删除父目录节点
			 zk.delete("/testRootPath",-1); 
			 Thread.sleep(1000);
			 // 关闭连接
			 zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.println("发生了"+event.getType()+"事件\n路径为："+event.getPath());
		
	}
}
