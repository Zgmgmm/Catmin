package me.zgmgmm.catmin;

public abstract class NIOEventHandler implements Runnable {
    Exception e=null;
    abstract public void handle() throws Exception;
    public void run() {
        try {
            handle();
        } catch (Exception e1) {
            e=e1;
            onException(e1);
        }finally {
            onFinish();
        }
    }

    public Exception getException() {
        return e;
    }

    public void onFinish(){
    }

    public void onException(Exception e){

    }
}
