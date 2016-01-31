package dna.central.config.entity;

import java.util.Set;

import org.apache.zookeeper.ZooKeeper;


/** 
* @author fengmuhai
* @date 2016年1月19日 上午11:44:14 
* @version 1.0  
*/
//zookeeper节点对象
public class Znode {

	private Znode root;
	private String data;
	private Set<Znode> subNode;
	private String pathName;
	private String nodeName;
	
	public Znode() {
		super();
	}

	public Znode(Znode root, String data) {
		super();
		this.root = root;
		this.data = data;
	}

	public Znode getNameRoot() {
		return root;
	}

	public void setNameRoot(Znode root) {
		this.root = root;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Set<Znode> getSubNode() {
		return subNode;
	}

	public void setSubNode(Set<Znode> subNode) {
		this.subNode = subNode;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}


	
	
	
}
