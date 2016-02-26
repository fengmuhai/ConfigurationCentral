package dna.central.httpClient;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author: XieminQuan
 * @time  : 2007-12-14 下午02:35:25
 *
 * DNAPAY
 */

public class ToolUtil {

    public static byte[] read(InputStream is,int length) throws Exception {
        
        byte[] result = new byte[length];
        int rNum = 0;
        while(rNum < result.length) {

            byte[] tt = new byte[result.length-rNum];
            
            int tmp = is.read(tt);
            
            if(tmp < 0) break;
            
            System.arraycopy(tt, 0, result, rNum, tmp);
            
            rNum += tmp;
        }
        
        return result;
    }
    
    public static String readSocketStr(HttpURLConnection connect) throws Exception {

        return new String(readSocket(connect.getInputStream()),"UTF-8");
    }
    
    public static byte[] readSocket(HttpURLConnection connect) throws Exception {

        return ToolUtil.readSocket(connect.getInputStream());
    }
    
    public static byte[] readSocket(InputStream is) throws Exception {
        BufferedInputStream in = new BufferedInputStream(is);
        LinkedList<Httpbuf> bufList = new LinkedList<Httpbuf>();
        int size = 0;
        byte buf[];
        
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if (num == -1)
                break;
            size += num;
            bufList.add(new Httpbuf(buf, num));
        } while (true);
        
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<Httpbuf> p = bufList.listIterator(); p.hasNext();) {
            
            Httpbuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }
        return buf;
    }
    
public static byte[] readSocket2(InputStream in) throws Exception {
        
        LinkedList<Httpbuf> bufList = new LinkedList<Httpbuf>();
        int size = 0;
        int retry = 5;
        
        for(int i = 0;i < 5;i++) {
            if(in.available() <= 0)
                Thread.sleep((i+1)*100);
            else
                break;
        }
        
        byte buf[];
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if(-1 == num){
                break;
            }
            
            size += num;
            bufList.add(new Httpbuf(buf, num));

            //lyq@2013-11-16 
            int unanum = 0;
            for(int i = 0; i<retry; i++) {
                int able = in.available();
                if(able <= 0){
                    unanum++;
                    Thread.sleep((i+1)*100);
                } else {
                    break;
                }
            }
            if(unanum >= retry){
                break;
            }
            
        } while (true);
        if(size > 0){
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Httpbuf> p = bufList.listIterator(); p.hasNext();) {
                
                Httpbuf b = p.next();
                for (int i = 0; i < b.size;) {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
    
            }
        }
        return buf;
    }
   

    public static String readHttpStr(HttpURLConnection connect) throws Exception {

        return new String(readHttp(connect.getInputStream()),"UTF-8");
    }
    
    public static byte[] readHttp(HttpURLConnection connect) throws Exception {

        return ToolUtil.readHttp(connect.getInputStream());
    }

    public static byte[] readHttp(InputStream in) throws Exception {

        LinkedList<Httpbuf> bufList = new LinkedList<Httpbuf>();
        int size = 0;
        byte buf[];
        
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if (num == -1)
                break;
            size += num;
            bufList.add(new Httpbuf(buf, num));
        } while (true);
        
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<Httpbuf> p = bufList.listIterator(); p.hasNext();) {
            
            Httpbuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }

        return buf;
    }


    public static String toString(Throwable e) {
        StringBuffer stack = new StringBuffer();
        stack.append(e);
        stack.append("\r\n");

        Throwable rootCause = e.getCause();

        while (rootCause != null) {
            stack.append("Root Cause:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            stack.append(rootCause.getMessage());
            stack.append("\r\n");
            stack.append("StackTrace:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            rootCause = rootCause.getCause();
        }


        for (int i = 0; i < e.getStackTrace().length; i++) {
            stack.append(e.getStackTrace()[i].toString());
            stack.append("\r\n");
        }
        return stack.toString();
    }
}

class Httpbuf
{

    public byte buf[];
    public int size;

    public Httpbuf(byte b[], int s)
    {
        buf = b;
        size = s;
    }
}