package me.zgmgmm.catmin;

import me.zgmgmm.catmin.Connector.Connector;

import java.io.IOException;

public class Bootstrap {
    public static void main(String[] args){
        try {
            System.out.println(System.getProperty("user.name"));
            System.out.println(System.getProperty("user.home"));
            System.out.println(System.getProperty("user.dir"));
            System.setProperty("user.dir","web");
            Connector connector=new Connector();
            connector.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
