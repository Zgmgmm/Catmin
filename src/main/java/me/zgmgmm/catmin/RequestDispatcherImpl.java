package me.zgmgmm.catmin;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestDispatcherImpl implements RequestDispatcher {
    protected static List<URLMapping> urlMap = new ArrayList<>();

    static {
        initUrlMap();
    }

    private Servlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    protected RequestDispatcherImpl(HttpServletRequest request, HttpServletResponse response, Servlet servlet) {
        this.servlet = servlet;
        this.request = request;
        this.response = response;
    }

    private static void initUrlMap() {
        //TODO
        URLMapping mapping = new URLMapping(new SimpleServlet(), "/");
        urlMap.add(mapping);
    }

    public static RequestDispatcher newInstance(HttpServletRequest req, HttpServletResponse res, String url) {
        Servlet servlet = null;
        for (URLMapping mapping : urlMap) {
            if (mapping.match(url)) {
                servlet = mapping.getServlet();
                break;
            }
        }
        if (servlet == null)
            return null;
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

    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

    }
}
