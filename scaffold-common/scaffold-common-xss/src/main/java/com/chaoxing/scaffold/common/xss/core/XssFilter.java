package com.chaoxing.scaffold.common.xss.core;

import com.chaoxing.scaffold.common.xss.cleaner.XssCleaner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class XssFilter extends GenericFilterBean {
    private final XssCleaner xssCleaner;
    private List<String> excludeUrls;

    /**
     * AntPath规则匹配器
     */
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    public XssFilter(XssCleaner xssCleaner) {
        this.xssCleaner = xssCleaner;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest,(HttpServletResponse) servletResponse,filterChain);
    }

    public void doFilter(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        if (requireFilter(request)){
            filterChain.doFilter(request,response);
            return;
        }
        XssStateHolder.open();
        try {
            filterChain.doFilter(new XssRequestWrapper(request, xssCleaner), response);
        } finally {
            // 必须删除 ThreadLocal 存储的状态
            XssStateHolder.remove();
        }
    }

    private boolean requireFilter(HttpServletRequest request){
        // 请求路径检查
        String requestUri = request.getRequestURI();
        // 此路径是否不需要处理
        for (String exclude : excludeUrls) {
            if (ANT_PATH_MATCHER.match(exclude, requestUri)) {
                return false;
            }
        }
        return true;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }
}
