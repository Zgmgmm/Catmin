package me.zgmgmm.catmin.Connector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Response implements HttpServletResponse {
    private final HashMap<String,List<String>> headers;
    ByteArrayOutputStream baos=new ByteArrayOutputStream();
    private String statusMeg;
    private int status=200;
    ServletOutputStream out;
    private PrintWriter writer;
    private boolean usingStream=false;
    private boolean usingWriter=false;

    public Response(){
        headers = new HashMap<>();
        out=new ServletOutputStreamAdapter(baos);
    }
    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public boolean containsHeader(String s) {
        return headers.containsKey(s);
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {
        status=i;
    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {
        setHeader(s,new Date(l).toGMTString());
    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String name, String value) {
        List<String> values=headers.get(name);
        if(values==null)
            values=new ArrayList<>();
        values.clear();
        values.add(value);
        headers.put(name,values);
    }

    @Override
    public void addHeader(String name, String value)  {
        List<String> values=headers.get(name);
        if(values==null)
            values=new ArrayList<>();
        values.add(value);
        headers.put(name,values);
    }

    @Override
    public void setIntHeader(String s, int i) {
        setHeader(s,i+"");
    }

    @Override
    public void addIntHeader(String s, int i) {
        addHeader(s,i+"");
    }

    @Override
    public void setStatus(int i) {
        status=i;
    }

    @Override
    public void setStatus(int i, String s) {
        status=i;
        statusMeg=s;
    }

    public int getStatus() {
        return status;
    }

    public String getHeader(String s) {
        List<String> values=headers.get(s);
        if(values==null)
            return null;
        return values.get(0);
    }

    public Collection<String> getHeaders(String name) {
        return this.headers.get(name);
    }

    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if(usingWriter)
            throw new IllegalStateException("Using Writer!");
        return out;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if(usingStream)
            throw new IllegalStateException("Using Stream!");
        if(writer==null)
            writer=new PrintWriter(out);
        return writer;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {
        setHeader("Content-Length",i+"");
    }

    public void setContentLengthLong(long l) {
        setHeader("Content-Length",l+"");
    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

}
