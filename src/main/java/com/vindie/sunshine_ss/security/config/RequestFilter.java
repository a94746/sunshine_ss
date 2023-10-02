package com.vindie.sunshine_ss.security.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Component
@Order(1)
public class RequestFilter implements Filter {
    public static final String MY_HEADER_NAME = "Sunshine-secret";
    @Value("${app.my-header-code}")
    private String myHeaderCode;

    public RequestFilter(@Value("${app.my-header-code}") String myHeaderCode) {
        if (isEmpty(myHeaderCode))
            throw new IllegalArgumentException("app.my-header-code is empty");
        this.myHeaderCode = myHeaderCode;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String code = ((HttpServletRequest) servletRequest).getHeader(MY_HEADER_NAME);
        if (!StringUtils.hasLength(code) || !myHeaderCode.equals(code)) {
            ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
