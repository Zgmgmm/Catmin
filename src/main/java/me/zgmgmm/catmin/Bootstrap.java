package me.zgmgmm.catmin;

import me.zgmgmm.catmin.Connector.Connector;

import java.io.IOException;

public class Bootstrap {
    public static void main(String[] args){
        try {
            System.setProperty("user.dir","web");
            Connector connector=new Connector();
            connector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
