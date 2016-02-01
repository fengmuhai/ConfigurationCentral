package dna.central.http.client_util;

import java.util.Map;

public class HttpClient {
	private HttpProxy proxy;
	
	public static void main(String[] args) {
		FunctionResult fr = new HttpClient("POST","http://127.0.0.1:8080/","utf-8",10000,10000,"hello world".getBytes(),null).send();
		System.out.println(fr.getRemark());
		System.out.println(new String((byte[])fr.getDataValue()));
	}
	
	/**
	 * 
	 * @param method POST/GET 传空默认为POST
	 * @param url 目标地址
	 * @param charset 编码，如GBK，UTF-8等默认UTF-8
	 * @param connecttimeout 连接超时，默认10秒，单位毫秒
	 * @param readtimeout 等待超时，默认10秒，单位毫秒
	 * @param data 发送数据
	 * @param headers 可定制HTTP头
	 */
	public HttpClient(String method, String url, String charset, int connecttimeout, int readtimeout, byte[] data, Map<String, String> headers){
		proxy = new HttpProxy(null, method, url, charset, null, data, headers);
		if(connecttimeout > 0){
			proxy.setConnecttimeout(connecttimeout);
			proxy.setReadtimeout(readtimeout);
		}
	}
	/**
	 * 通过HTTP代理服务器中转发送请求
	 * @return fr
	 * fr.ReturnValue HTTP状态码 默认值 T101 网络异常或者其他异常
	 * fr.Remark 状态码返回的相应的消息，例如OK, Bad GateWay等，如果异常则是报错的异常说明
	 * fr.DataValue 服务器返回的正文内容，返回字节数据byte[]，如果没有或者失败则空
	 * 
	 */
	public FunctionResult send(){
		return proxy.send();
	}
}