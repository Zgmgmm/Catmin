package me.zgmgmm.catmin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultErrorHandler extends HttpServlet {
    public static final String FILE_NOT_FOUND_RESPONSE="404 NOT FOUND!";
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int status=resp.getStatus();
        switch (status){
            case 404:
                resp.getWriter().write(FILE_NOT_FOUND_RESPONSE);
                default:
                    resp.setStatus(500);
        }
    }
}
