package me.zgmgmm.catmin;

import me.zgmgmm.catmin.Exception.BadRequestException;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpUtil {
    public static void parseHeaders(Map<String,String> target, String data) throws BadRequestException {
        StringTokenizer tokenizer=new StringTokenizer(data,"\r\n");
        while(tokenizer.hasMoreElements()){
            String header=tokenizer.nextToken();
            int i=header.indexOf(":");
            if(i!=-1){
                target.put(header.substring(0,i),header.substring(i+2));
            }else {
                throw new BadRequestException("Illegal header format: "+data);
            }
        }
    }
}
