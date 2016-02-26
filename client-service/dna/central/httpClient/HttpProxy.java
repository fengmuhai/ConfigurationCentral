package dna.central.httpClient;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
/**
 * 2016-01-16
 * 通过标准的HTTP代理协议中转请求
 * @author luoyunqiu
 *
 */
public class HttpProxy {
	private String proxy;
	private int proxyPort;
	private int connecttimeout = 10 * 1000;
	private int readtimeout = 10 * 1000;
	private String method = "POST";
	private String url;
	private String charset = "UTF-8";
	private String sslversion = "TLS";
	private byte[] data;
	private Map<String, String> headers = null;
	
	public int getConnecttimeout() {
		return connecttimeout;
	}
	public void setConnecttimeout(int connecttimeout) {
		this.connecttimeout = connecttimeout;
	}
	public int getReadtimeout() {
		return readtimeout;
	}
	public void setReadtimeout(int readtimeout) {
		this.readtimeout = readtimeout;
	}
	/**
	 * 
	 * @param proxy IP:PORT:连接超时:等待超时(单位毫秒) 如果不需要走中转可填空
	 * @param method GET/POST
	 * @param url 
	 * @param charset
	 * @param sslversion SSL版本，默认值TLS 可选值 SSL/TLS
	 * @param data
	 * @param headers 需要新增特殊的HTTP头
	 */
	public HttpProxy(String proxy, String method, String url, String charset, String sslversion, byte[] data, Map<String, String> headers){
		//IP:PORT:连接超时:等待超时(单位毫秒)
		if(null != proxy 
				&& !"".equals(proxy)){
			String[] proxyinfo = proxy.split(":");
			this.proxy = proxyinfo[0];
			this.proxyPort = Integer.parseInt(proxyinfo[1].trim());
			if(proxyinfo.length > 2)
				connecttimeout = Integer.parseInt(proxyinfo[2].trim());
			if(proxyinfo.length > 3)
				readtimeout = Integer.parseInt(proxyinfo[3].trim());
		}
		this.method = method;
		this.url = url;
		this.charset = charset;
		this.data = data;
		this.headers = headers;
		if(null != sslversion 
				&& !"".equals(sslversion.trim())){
			this.sslversion = sslversion;
		}
	}
	/**
	 * 通过HTTP代理服务器中转发送请求
	 * @return fr
	 * fr.ReturnValue HTTP状态码 默认值 T101 网络异常或者其他异常
	 * fr.Remark 状态码返回的相应的消息，例如OK, Bad GateWay等，如果异常则是报错的异常说明
	 * fr.DataValue 服务器返回的正文内容
	 * 
	 */
	public FunctionResult send(){
		FunctionResult fr = new FunctionResult(0, "T101", "未知原因");
		HttpURLConnection connect = null;
		try {
			Proxy py = null;
			if(null != proxy && !"".equals(proxy.trim())){
				InetSocketAddress addr = new InetSocketAddress(proxy, proxyPort);
				py = new Proxy(Proxy.Type.HTTP, addr);
			}
			if("GET".equals(this.method)){
				if(null != data){
					String req = new String(data, this.charset);
					if(!"".equals(req.trim())){
						this.url += "?"+new String(data, this.charset);
					}
				}
			}
        	if(this.url.startsWith("https")){
        		connect = new SslConnection().openConnection(py, this.sslversion, this.url);
        	} else {
        		URL u = new URL(this.url);
        		if(null != py){
        			connect = (HttpURLConnection) u.openConnection(py);
        		} else {
        			connect = (HttpURLConnection) u.openConnection();
        		}
        	}
        	if(null != this.headers){
        		for(Map.Entry<String, String> header: headers.entrySet()){
        			connect.setRequestProperty(header.getKey(), header.getValue());
        		}
        	}
        	connect.setRequestMethod(this.method);
        	connect.setConnectTimeout(this.connecttimeout);
			connect.setReadTimeout(this.readtimeout);
			if("POST".equals(this.method)){
				connect.setDoOutput(true);
			}
			connect.setDoInput(true);
			connect.connect();
			if("POST".equals(this.method)){
				connect.getOutputStream().write(data);
				connect.getOutputStream().flush();
				connect.getOutputStream().close();
			}
			fr.setReturnValue(connect.getResponseCode());
			fr.setRemark(connect.getResponseMessage());
			if(connect.getResponseCode() == HttpURLConnection.HTTP_OK){
				byte[] res = ToolUtil.readHttp(connect);
				fr.setDataValue(res);
			}
		} catch(Exception e){
			fr.setRemark(e.getMessage());
		} finally {
			if(null != connect){
				connect.disconnect();
			}
		}
		return fr;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}