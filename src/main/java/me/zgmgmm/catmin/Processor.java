package me.zgmgmm.catmin;


import org.apache.log4j.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.swing.text.html.HTMLDocument;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class Processor implements Runnable {
    public static Logger logger=Logger.getLogger(Processor.class);
    private Connector connector;
    private SocketChannel channel;
    private Request request;
    private Response response;
    private ByteBuffer bb;
    private Exception exception;
    private int startLineEnd;

    public Processor(Connector connector) {
        this.connector = connector;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public void process() throws IOException {
        channel.configureBlocking(false);
        request=new Request();
        response=new Response();
        response.setRequest(request);
        bb =ByteBuffer.allocate(4096);
        bb.mark();
        readAllBytes();
       // readStartLine();
    }

    private void readAllBytes() throws ClosedChannelException {
        SelectionKey key=connector.register(channel,SelectionKey.OP_READ);
        key.attach((OnReadableListener) () -> {
            int readBytes=0;
            try {
                readBytes=channel.read(bb);
            } catch (IOException e) {
                e.printStackTrace();
                key.cancel();
            }
            if(readBytes==-1){
                try {
                    channel.write(bb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
              //  key.cancel();

            }
        });
    }

    private void readHeader() throws ClosedChannelException {
        SelectionKey key=connector.register(channel,SelectionKey.OP_READ);
        key.attach((OnReadableListener) () -> {
            bb.mark();
            int readBytes=0;
            try {
                readBytes=channel.read(bb);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }

            if(readBytes==-1) {
                key.cancel();
                return;
            }

            boolean headerEnd=false;
            byte[] b=new byte[4];
            int end = bb.position();
            bb.reset();
            while (bb.position()< end){
                b[3] = bb.get();
                if(Arrays.equals(b,new byte[]{13,10,13,10})){
                    headerEnd=true;
                    break;
                }
                b[0]=b[1];
                b[1]=b[2];
                b[2]=b[3];
            }
            if(headerEnd){
                //parse header
                byte[] arr=bb.array();
                String raw=new String(arr,startLineEnd+2,bb.position()-startLineEnd-4);
                String[] headers=raw.split("\r\n");
                for(String header:headers){
                    String[] pair=header.split(": ");
                    request.setHeader(pair[0],pair[1]);
                }
                logger.info(raw);
                ByteArrayInputStream bis=new ByteArrayInputStream(bb.array(),bb.position(),end-bb.position());
                bis.skip(bb.position());
                request.setInputStream(new DefaultServletInputStream(bis));
                try {
                    new SimpleServlet().service(request,response);
                } catch (ServletException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    readHeader();
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    private void readStartLine() throws IOException {
        SelectionKey key=connector.register(channel,SelectionKey.OP_READ);
        key.attach((OnReadableListener) () -> {
            bb.mark();
            int readBytes=0;
            try {
                readBytes=channel.read(bb);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(readBytes==-1) {//no more bytes
                key.cancel();
                return;
            }

            //scan new read bytes for line separator
            boolean lineEnd=false;
            byte pre=0;//last byte
            byte cur=0;//current byte
            int end = bb.position();
            bb.reset(); //start at the begin of new read bytes
            while (bb.position()< end){
                cur = bb.get();
                if(pre=='\r'&&cur=='\n'){
                    lineEnd=true;
                    break;
                }
                pre=cur;
            }

            if(lineEnd) {//finish
                startLineEnd=bb.position()-2;
                request.setStartLine(bb.array(),0,startLineEnd);
                logger.info(new String(bb.array(),0,startLineEnd));
                try {
                    readHeader();
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    readStartLine();  //continue reading

                } catch (IOException e) {
                    e.printStackTrace();
                    exception=e;
                }
            }
        });
    }
    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getLocalizedMessage());
        }
    }
}
