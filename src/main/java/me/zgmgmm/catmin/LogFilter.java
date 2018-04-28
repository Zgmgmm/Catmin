package me.zgmgmm.catmin;

import me.zgmgmm.catmin.Connector.Filter;
import me.zgmgmm.catmin.Connector.FilterChain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogFilter implements Filter {
   // public static final Logger logger=Logger.getRootLogger();
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println(request.getRemoteAddr()+" request "+"url: "+request.getRequestURI());
        //logger.info(request.getRemoteAddr()+" request "+"url: "+request.getRequestURI());
        chain.doFilterChain(request,response);
        System.out.println(request.getRemoteAddr()+" finish");
        //logger.info(request.getRemoteAddr()+" finish");
    }
}
