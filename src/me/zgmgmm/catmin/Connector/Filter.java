package me.zgmgmm.catmin.Connector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Filter {
    void doFilter(HttpServletRequest request, HttpServletResponse response,FilterChain chain) throws ServletException, IOException;
}
