package me.zgmgmm.catmin.Connector;


import me.zgmgmm.catmin.Exception.BadRequestException;
import me.zgmgmm.catmin.HttpUtil;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;

public class Processor implements Runnable {
    private static byte[] CRLF={13,10,13,10};
    public static Logger logger=Logger.getRootLogger();
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

    public void init() throws IOException {
        channel.configureBlocking(false);
        request=new Request();
        response=new Response();
        request.channel=channel;
        request.response=response;
        bb =ByteBuffer.allocate(4096);
    }
    public void process() throws IOException {
        init();
        logger.debug(channel.socket().getRemoteSocketAddress()+" register read");
        connector.register(channel, SelectionKey.OP_READ ,new NIOEventHandler() {
            @Override
            public void handle() throws Exception {
                int from=bb.position();
                int readBytes=0;
                readBytes=channel.read(bb);
                logger.debug("read "+readBytes);

                //check if \r\n\r\n has been read
                boolean isHeadersEnd=false;

                if(readBytes==-1) {
                    isHeadersEnd = true;
                }else {
                    int i=from;
                    int end=bb.position();
                    byte[] arr = bb.array();
                    while (i + 3 < end) {
                        if (arr[i]=='\r'&&arr[i+1]=='\n'&&arr[i+2]=='\r'&&arr[i+3]=='\n') {
                            isHeadersEnd = true;
                            break;
                        }
                        i++;
                    }
                }
                if(!isHeadersEnd){
                    prepareRequest();
                }else {
                    parseRequest();
                    RequestWrapper request=new RequestWrapper(Processor.this.request);
                    ResponseWrapper response=new ResponseWrapper(Processor.this.response);
                    request.getRequestDispatcher(request.getPathInfo()).forward(request,response);
                    sendResponse();
                }
            }
            @Override
            public void onException(Exception e){
                if(e!=null){
                    //TODO
                    try {
                        channel.close();
                    }catch(IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendResponse() throws ClosedChannelException {
        //send headers
        ByteBuffer[] bufs=new ByteBuffer[2];
        bufs[1]=ByteBuffer.wrap(Processor.this.response.baos.toByteArray());

        //add headers if needed
        if(!response.containsHeader("Content-Length")&&!response.containsHeader("Transfer-Encoding")){
            response.setContentLength(bufs[1].remaining());
        }

        StringBuilder builder=new StringBuilder();
        builder.append("HTTP/1.1 "+response.getStatus()+"\r\n");
        Collection<String> names=response.getHeaderNames();
        names.forEach(
                name-> response.getHeaders(name).forEach(
                        value->builder.append(name + ": " +value+"\r\n")
                )
        );
        builder.append("\r\n");
        bufs[0]=ByteBuffer.wrap(builder.toString().getBytes());
        //send body

        connector.register(channel, SelectionKey.OP_WRITE, new NIOEventHandler() {
            @Override
            public void handle() throws Exception {
                channel.write(bufs);
                if(bufs[bufs.length-1].hasRemaining()) {
                    sendResponse();
                } else{
                    finish();
                }
            }
        });
    }

    private void finish() throws IOException {
        channel.close();
    }

    private void prepareRequest() throws ClosedChannelException {
        //TODO
    }

    private void parseRequest() throws BadRequestException {
        byte[] arr=bb.array();
        int end=bb.position();
        int i=0;

        //parse request line
        while(i+1<end&&!(arr[i]=='\r'&&arr[i+1]=='\n'))++i;
        //TODO
        // need to check its validation
        request.setRequestLine(arr,0,i);
        i+=2;//skip the next four bytes {\n\r\n};

        int headerStart=i;
        //find the headers end
        while(i+3<end&&!(arr[i]=='\r'&&arr[i+1]=='\n'&&arr[i+2]=='\r'&&arr[i+3]=='\n'))++i;
        HttpUtil.parseHeaders(request.headers,new String(arr,headerStart,i-2-headerStart));

        //set body buffer
        int bodyStart=i+4;
        request.bais =new ByteArrayInputStream(arr,bodyStart,end-bodyStart);

        Iterator it=request.headers.entrySet().iterator();
        while (it.hasNext()) {
            logger.debug(it.next());
        }
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
