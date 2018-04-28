package me.zgmgmm.catmin;

import me.zgmgmm.catmin.Connector.FilterChain;
import me.zgmgmm.catmin.Connector.URLMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class RequestDispatcherImpl implements RequestDispatcher {
    protected static List<URLMapping> urlMap = new ArrayList<>();
    protected String servletDir=System.getProperty("user.dir")+ File.separator+"WEB-INF"+File.separator+"Servlet";
    static {
        initUrlMap();
    }
    private static void initUrlMap() {
        //TODO load web.xml
        URLMapping mapping = new URLMapping(new DefaultServlet(), ".*");
        urlMap.add(mapping);
    }


    private Servlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private me.zgmgmm.catmin.Connector.FilterChain chain;

    protected RequestDispatcherImpl(HttpServletRequest request, HttpServletResponse response, Servlet servlet) {
        this.servlet = servlet;
        this.request = request;
        this.response = response;
        chain=new FilterChain(servlet);
        chain.addFilter(new LogFilter());
    }


    private static Servlet getServlet(String url) {
        for (URLMapping mapping : urlMap) {
            if (mapping.match(url)) {
                return mapping.getServlet();
            }
        }
        return null;
    }
    public static RequestDispatcher newInstance(HttpServletRequest req, HttpServletResponse res, String url){
        Servlet servlet = getServlet(url);
        return new RequestDispatcherImpl(req, res, servlet);
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //TODO
        System.out.println("forward "+servlet);
        chain.doFilterChain(request,response);
    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //TODO
        chain.doFilterChain(request,response);
    }
}
