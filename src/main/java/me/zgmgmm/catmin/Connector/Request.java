package me.zgmgmm.catmin.Connector;

import me.zgmgmm.catmin.RequestDispatcherImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;
import java.security.Principal;
import java.util.*;

public class Request implements HttpServletRequest {
    SocketChannel channel;
    Response response;
    byte[] requestLine;
    int methodEnd;
    int uriEnd;
    int schemeEnd;
    ByteArrayInputStream bais;
    Map<String, String> headers;
    private ServletInputStream in;

    public Request() {
        requestLine = new byte[512];
        headers = new HashMap<>();
        in= new ServletInputStreamAdapter(bais);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    public void setRequestLine(byte[] startLine) {
        setRequestLine(startLine, 0, startLine.length);
    }

    public void setRequestLine(byte[] startLine, int offset, int length) {
        if (length > this.requestLine.length)
            this.requestLine = Arrays.copyOfRange(startLine, offset, length);
        else
            System.arraycopy(startLine, offset, this.requestLine, 0, length);
        //parse
        int i = 0;
        while (i < length - offset && this.requestLine[i++] != ' ') ;
        methodEnd = i - 1;
        while (i < length - offset && this.requestLine[i++] != ' ') ;
        uriEnd = i - 1;
        schemeEnd = length - offset;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getMethod() {
        return new String(requestLine, 0, methodEnd);
    }

    @Override
    public String getPathInfo() {
        return new String(requestLine, methodEnd + 1, uriEnd - methodEnd - 1);
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return getPathInfo();
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }


    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }


    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        //TODO
        return in;
    }


    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return new String(requestLine, uriEnd + 1, schemeEnd);
    }

    @Override
    public String getScheme() {
        return getProtocol().indexOf('s') == -1 ? "HTTP" : "HTTPS";
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return channel.socket().getRemoteSocketAddress().toString();
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String url) {
        return  RequestDispatcherImpl.newInstance(this,response,url);
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

}
