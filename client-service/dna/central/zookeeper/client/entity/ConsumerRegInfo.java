package dna.central.zookeeper.client.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("consumerRegInfo")
public class ConsumerRegInfo {

	@XStreamAlias("zookeeper")
	private String zookeeperUrl;
	@XStreamAlias("configPath")
	private String configPath;
	
	public ConsumerRegInfo() {
		
	}
	
	public ConsumerRegInfo(String zookeeperUrl, String configPath) {
		super();
		this.zookeeperUrl = zookeeperUrl;
		this.configPath = configPath;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	
	
}
