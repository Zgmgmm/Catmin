package me.zgmgmm.catmin;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;


public class Connector {
    public static Connector instance;
    public static final Logger logger = Logger.getLogger(Connector.class);
    InetSocketAddress isa;
    Selector selector;
    private boolean isShutdown;
    private Executor executor;
    private ServerSocketChannel ssc;
    private Semaphore semaphore=new Semaphore(1);
    private AtomicBoolean selectorLock=new AtomicBoolean(false);
    private SelectorHelper selectorHelper;

    public static Connector getConnector(){
        if(instance==null) {
            try {
                instance=new Connector();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connector() throws IOException {
        isa = new InetSocketAddress("localhost", 80);
        init();
    }

    public static void main(String[] args) throws IOException {
        Connector connector = Connector.getConnector();
        connector.start();
    }

    private void init() throws IOException {
        ssc = ServerSocketChannel.open();
        selector = Selector.open();
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        logger.setLevel(Level.INFO);
        logger.info("connector starting");
        isShutdown = false;
        try {
            ssc.bind(isa);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            return;
        }
        executor.execute(() -> {
            selectorHelper=new SelectorHelper(selector);
            while (!isShutdown) {
                int numKeys = 0;
                try {
                    numKeys = selectorHelper.select();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getLocalizedMessage());
                    return;
                }
                if (numKeys == 0)
                    continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    logger.debug(key.channel().toString() + " is readable.");
                    if (key.isReadable()) {
                        //TODO
                        OnReadableListener listener= (OnReadableListener) key.attachment();
                        if(listener!=null)
                            listener.onReadable();
                    }
                    else if (key.isWritable()) {
                        //TODO
                        logger.debug(key.channel().toString() + " is writable.");
                    }
                    it.remove();
                }
            }
        });


        while (!isShutdown) {
            try {
                SocketChannel channel = ssc.accept();
                executor.execute(newProcessor(channel));
            } catch (IOException e) {
                e.printStackTrace();
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

    public Selector getSelector() {
        return selector;
    }

    public Logger getLogger() {
        return logger;
    }

    public SelectionKey register(SelectableChannel channel, int ops) throws ClosedChannelException {
        return selectorHelper.register(channel,ops);
    }
    public synchronized SelectionKey register(SelectableChannel channel, int ops, Object attachment) throws ClosedChannelException {
        return selectorHelper.register(channel,ops,attachment);
    }

    public class SelectorHelper {
        private volatile boolean mark = false;
        private final Selector selector;

        public SelectorHelper(Selector selector) {
            this.selector = selector;
        }

        public Selector getSelector() {
            return selector;
        }

        public SelectionKey register(SelectableChannel channel, int ops) throws ClosedChannelException {
            return register(channel,ops,null);
        }

        /**
         * 必须是同步的， 保证多个线程调用reg的时候不会出现问题
         * @param channel
         * @param ops
         * @param attachment
         * @return
         * @throws ClosedChannelException
         */
        public synchronized SelectionKey register(SelectableChannel channel, int ops, Object attachment) throws ClosedChannelException {
            mark = true;
            selector.wakeup();
            SelectionKey key = channel.register(selector, ops, attachment);
            mark = false;
            return key;
        }

        public int select() throws IOException {
            for (;;) {
                if (mark == true)
                    continue;
                int select = selector.select();
                if (select > 0)
                    return select;
            }
        }
    }

}
