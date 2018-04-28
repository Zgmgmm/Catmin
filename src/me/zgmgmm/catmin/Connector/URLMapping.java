package me.zgmgmm.catmin.Connector;

import javax.servlet.Servlet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class URLMapping {
    Set<String> patterns = new HashSet<>();
    Servlet servlet;

    public URLMapping() {

    }

    public URLMapping(Servlet servlet) {
        this.servlet = servlet;
    }

    public URLMapping(Servlet servlet, List<String> patterns) {
        this(servlet);
        if (patterns != null)
            patterns.addAll(patterns);
    }

    public URLMapping(Servlet servlet, String pattern) {
        this(servlet);
        if (pattern != null)
            patterns.add(pattern);
    }

    public static boolean match(String url, String pat) { return url.matches(pat);
    }

    public void addPattern(String pattern) {
        patterns.addAll(patterns);
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void addPatterns(Collection<String> patterns) {
        patterns.addAll(patterns);
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public boolean match(String url) {
        for (String pat : patterns)
            if (match(url, pat))
                return true;
        return false;
    }
}
