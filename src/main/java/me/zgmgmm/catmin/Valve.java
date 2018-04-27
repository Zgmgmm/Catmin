package me.zgmgmm.catmin;

public interface Valve {
    public Valve getNext();
    public void setNext(Valve valve);
    public void invoke(Request request, Response response);
}

