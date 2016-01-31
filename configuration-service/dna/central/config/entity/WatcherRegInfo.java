package dna.central.config.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("watcherRegInfo")
public class WatcherRegInfo {

	@XStreamAlias("zookeeper")
	private String zookeeperUrl;
	@XStreamAlias("watcherId")
	private String watcherId;
	
	public WatcherRegInfo() {
		
	}
	
	public WatcherRegInfo(String zookeeperUrl, String watcherId) {
		super();
		this.zookeeperUrl = zookeeperUrl;
		this.watcherId = watcherId;
	}

	public String getZookeeperUrl() {
		return zookeeperUrl;
	}

	public void setZookeeperUrl(String zookeeperUrl) {
		this.zookeeperUrl = zookeeperUrl;
	}

	public String getWatcherId() {
		return watcherId;
	}

	public void setWatcherId(String watcherId) {
		this.watcherId = watcherId;
	}

	
	
}
