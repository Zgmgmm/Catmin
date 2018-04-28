package me.zgmgmm.catmin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultServlet extends HttpServlet {
    public static String WEB_ROOT= System.getProperty("user.dir");
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path path = Paths.get(WEB_ROOT,req.getPathInfo().substring(1));
        File file=path.toFile();
        System.out.println(file.getAbsolutePath());
        if(!file.exists()||!file.isFile()){
            resp.sendError(404);
            return;
        }
        long time=file.lastModified();
        resp.setDateHeader("Last-Modified",time);
        OutputStream out=resp.getOutputStream();
        FileInputStream fis=new FileInputStream(file);
        byte[] buf=new byte[1024];
        while(fis.read(buf)!=-1){
            out.write(buf);
        }
        fis.close();
    }
}
