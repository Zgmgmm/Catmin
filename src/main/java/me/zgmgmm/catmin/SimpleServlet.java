package me.zgmgmm.catmin;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class SimpleServlet extends HttpServlet {

    public SimpleServlet(){
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream in=req.getInputStream();
        byte[] buf=new byte[1024];
        in.read(buf);
        OutputStream out=resp.getOutputStream();
        out.write("<h1>Hello</h1>".getBytes());
        String body=new String(buf);
        Logger.getRootLogger().debug(body);
    }
}
