package me.zgmgmm.catmin.Connector;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Level;

public class Connector {
    public static final Logger logger = Logger.getRootLogger();
    public static final String DEFAULT_HOST="0.0.0.0";
    public static final int DEFAULT_PORT=80;
    static {
        logger.setLevel(Level.INFO);
    }
    public static Connector instance;
    InetSocketAddress isa;
    Selector selector;
    private ThreadPoolExecutor threadPool;
    private ServerSocketChannel ssc;
    private SelectorHelper selectorHelper;
    private int corePoolSize = 5;
    private int maximumPoolSize = 500;
    private long keepAliveTime = 5;
    private boolean isShutdown = false;
    private int queueCapacity=5000;

    public Connector() throws IOException {
        this(DEFAULT_HOST,DEFAULT_PORT);
    }

    public Connector(String host,int port) throws IOException {
        isa = new InetSocketAddress("0.0.0.0", 80);
        isShutdown = false;
        ssc = ServerSocketChannel.open();
        selector = Selector.open();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.DiscardPolicy());
    }
    public static Connector getConnector() {
        if (instance == null) {
            try {
                instance = new Connector();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void main(String[] args) throws IOException {
        Connector connector = Connector.getConnector();
        connector.start();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }



    public void start() throws IOException {
        logger.info("connector starting");
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(isa);
        selectorHelper = new SelectorHelper(selector);
        while (!isShutdown) {
            int numKeys = 0;
            try {
                numKeys = selectorHelper.select();
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug(e.getLocalizedMessage());
                return;
            }
            if (numKeys == 0)
                continue;
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SocketChannel channel;
                SelectionKey key = it.next();
                it.remove();
                if(!key.isValid())
                    continue;
                if (key.isAcceptable()) {
                    channel = ssc.accept();
                    if(channel==null)
                        continue;
                    channel.configureBlocking(false);
                    threadPool.execute(newProcessor(channel));
                    continue;
                }
                channel= (SocketChannel) key.channel();
                if (key.isReadable()||key.isWritable()) {
                    NIOEventHandler handler = (NIOEventHandler) key.attachment();
                    logger.debug(channel.socket().getRemoteSocketAddress() + " is writable.");
                    threadPool.submit(handler);
                }
                key.cancel();
            }
        }

    }

    public void shutdown() throws IOException {
        synchronized (ssc) {
            if (isShutdown)
                return;
            isShutdown = true;
            ssc.close();
        }
    }

    private Processor newProcessor(SocketChannel channel) {
        try {
            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Processor processor = new Processor(this);
        processor.setChannel(channel);
        return processor;
    }


    public SelectionKey register(SelectableChannel channel, int ops) throws ClosedChannelException {
        return selectorHelper.register(channel, ops);
    }

    public synchronized SelectionKey register(SelectableChannel channel, int ops, NIOEventHandler handler) throws ClosedChannelException {
        return selectorHelper.register(channel, ops, handler);
    }

    public class SelectorHelper {
        private final Selector selector;
        private volatile boolean mark = false;

        public SelectorHelper(Selector selector) {
            this.selector = selector;
        }

        public Selector getSelector() {
            return selector;
        }

        public SelectionKey register(SelectableChannel channel, int ops) throws ClosedChannelException {
            return register(channel, ops, null);
        }

        /**
         * 必须是同步的， 保证多个线程调用reg的时候不会出现问题
         *
         * @param channel
         * @param ops
         * @param attachment
         * @return
         * @throws ClosedChannelException
         */
        public synchronized SelectionKey register(SelectableChannel channel, int ops, Object attachment) throws ClosedChannelException {
            mark = true;
            selector.wakeup();
            SelectionKey key = channel.keyFor(selector);
            if(key==null)
                channel.register(selector, ops, attachment);
            mark = false;
            return key;
        }

        public int select() throws IOException {
            for (; ; ) {
                while (mark);

                int select = selector.select();
                if (select > 0)
                    return select;
            }
        }
    }

}
