package me.zgmgmm.catmin;


public class ProcessPipeline implements Pipeline{
    Valve first;
    Valve last;

    @Override
    public void addValve(Valve valve) {
        last.setNext(valve);
        last=valve;
    }

    @Override
    public Valve getFirst() {
        return first;
    }
}
