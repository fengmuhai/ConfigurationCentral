package dna.central.zookeeper.test;
/** 
 * @author fengmuhai
 * @date 2016-3-14 下午5:22:58 
 * @version 1.0  
 */
public class RoundRobinTest extends Thread{
	
	public static int order = 0;

	public static void main(String[] args) {
		for(int i=Integer.MAX_VALUE+1;i<Integer.MAX_VALUE+10;i++){
			new RoundRobinTest().start();
		}
		//System.out.println(Integer.MAX_VALUE+2);
	}

	public static synchronized int getOrder() {
		//order++;
		return order++;
	}
	public static synchronized void addOrder() {
		order++;
	}

	@Override
	public void run() {
		int x = getOrder();
		//printOrder(x);
		if(x%3==0){
			printOrder0();
			//addOrder();
		} else if(x%3==1) {
			printOrder1();
			//addOrder();
		} else if(x%3==2) {
			printOrder2();
			//addOrder();
		}
	}
	
	public static void printOrder(int i) {
		System.out.println(Thread.currentThread().getName()+"执行第"+i+"号服务！");
	}
	
	public static void printOrder0() {
		System.out.println(Thread.currentThread().getName()+"执行第"+0+"号服务！");
	}
	public static void printOrder1() {
		System.out.println(Thread.currentThread().getName()+"执行第"+1+"号服务！");
	}
	public static void printOrder2() {
		System.out.println(Thread.currentThread().getName()+"执行第"+2+"号服务！");
	}
}
