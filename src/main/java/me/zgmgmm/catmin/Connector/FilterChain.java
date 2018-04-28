package me.zgmgmm.catmin.Connector;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FilterChain {
    private List<Filter> filters;
    private Iterator<Filter> it;
    private Servlet servlet;

    public FilterChain(){
        filters=new ArrayList<>();
    }
    public FilterChain(Servlet servlet) {
        this();
        this.servlet=servlet;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public void doFilterChain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(it==null)
            it=filters.iterator();
        if(it.hasNext()){
            it.next().doFilter(request,response,this);
            System.out.println(it.toString());
        }else{
            System.out.println(servlet.toString());
            servlet.service(request,response);
        }
    }
    public void addFilter(Filter filter){
        filters.add(filter);
    }
}
