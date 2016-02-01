package dna.central.zookeeper.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import dna.central.zookeeper.client.entity.Message;

/** 
* @author fengmuhai
* @date 2016年1月22日 上午11:48:11 
* @version 1.0  
*/
public class HttpUtil {
	
	public static void main(String[] args) {
		
	}

	@SuppressWarnings("finally")
	public static String sendPost(String transMes) {
		String responseMessage = null;
		try {
			URL url = new URL("http://127.0.0.1:8080/CBEC/CEACCINF.do");
			URLConnection conn = url.openConnection();
			HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
			httpUrlConnection.setRequestMethod("POST");
			byte[] dataByte = transMes.getBytes("gbk");
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoInput(true);
	        httpUrlConnection.setInstanceFollowRedirects(false);
	        httpUrlConnection.addRequestProperty("Content-Type", "application/json;charset=gbk");
	        httpUrlConnection.addRequestProperty("Content-Length", String.valueOf(dataByte.length));
	        OutputStream out = httpUrlConnection.getOutputStream();
	        out.write(dataByte);
	        out.flush();
	        out.close();
	        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "gbk");
	        BufferedReader in = new BufferedReader(inputStreamReader);
	        
	        /*解读返回数据*/
	        String line = null;
	        StringBuilder stringBuilder = new StringBuilder(255);
	        while((line = in.readLine())!=null){
	        	stringBuilder.append(line);
	        	stringBuilder.append("\n");
	        }
	        responseMessage = stringBuilder.toString();
	        System.out.println("responseMessage: "+responseMessage);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return responseMessage;
		}
	}
		
}
