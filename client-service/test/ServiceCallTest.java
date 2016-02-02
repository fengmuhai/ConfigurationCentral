package test;

import dna.central.zookeeper.client.ZkClientConsumer;
import dna.central.zookeeper.client.util.JacksonTools;

public class ServiceCallTest {

	public static void main(String[] args) {
		final String jstr = JacksonTools.jsonFileToString("client-service/message.json");
		//System.out.println(ZkClientConsumer.call(jstr));
		for(int i=0;i<10;i++){
			new Thread() {
				public void run() {
					System.out.println(ZkClientConsumer.call(jstr));
					try {
						this.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
