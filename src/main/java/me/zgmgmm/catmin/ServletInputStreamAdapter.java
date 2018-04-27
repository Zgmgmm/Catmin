package me.zgmgmm.catmin;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ServletInputStreamAdapter extends ServletInputStream {
    ByteArrayInputStream bais;
    public ServletInputStreamAdapter(ByteArrayInputStream bais){
        this.bais =bais;
    }
    @Override
    public boolean isFinished() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
        return bais.read();
    }
}
