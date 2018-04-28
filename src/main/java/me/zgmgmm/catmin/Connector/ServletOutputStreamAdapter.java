package me.zgmgmm.catmin.Connector;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletOutputStreamAdapter extends ServletOutputStream {
    ByteArrayOutputStream baos;
    public ServletOutputStreamAdapter(ByteArrayOutputStream baos){
        this.baos=baos;
    }

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }
}
