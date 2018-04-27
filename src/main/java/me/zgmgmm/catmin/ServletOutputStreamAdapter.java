package me.zgmgmm.catmin;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletOutputStreamAdapter extends ServletOutputStream {
    ByteArrayOutputStream baos;
    public ServletOutputStreamAdapter(ByteArrayOutputStream baos){
        this.baos=baos;
    }
    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }
}
