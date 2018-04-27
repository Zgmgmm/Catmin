package me.zgmgmm.catmin;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DefaultServletInputStream extends ServletInputStream {
    ByteArrayInputStream bis;
    public DefaultServletInputStream(ByteArrayInputStream bis){
        this.bis=bis;
    }
    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return bis.read();
    }
}
