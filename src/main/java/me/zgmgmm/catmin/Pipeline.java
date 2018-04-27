package me.zgmgmm.catmin;

public interface Pipeline {
    public void addValve(Valve valve);
    public Valve getFirst();
}
