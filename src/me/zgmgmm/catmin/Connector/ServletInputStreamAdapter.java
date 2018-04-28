package me.zgmgmm.catmin.Connector;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ServletInputStreamAdapter extends ServletInputStream {
    ByteArrayInputStream bais;
    public ServletInputStreamAdapter(ByteArrayInputStream bais){
        this.bais =bais;
    }

    @Override
    public int read() throws IOException {
        return bais.read();
    }
}
