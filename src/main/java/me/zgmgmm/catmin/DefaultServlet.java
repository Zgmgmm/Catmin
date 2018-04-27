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

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path path=Paths.get(System.getProperty("user.dir"),req.getPathInfo());
        File file=path.toFile();
        if(!file.exists()||!file.isFile()){
            resp.setStatus(404);
        }
        OutputStream out=resp.getOutputStream();
        FileInputStream fis=new FileInputStream(file);
        long transferred = 0;
        byte[] buffer = new byte[1024];
        int read;
        while ((read = fis.read(buffer, 0, 1024)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        fis.close();
    }
}
