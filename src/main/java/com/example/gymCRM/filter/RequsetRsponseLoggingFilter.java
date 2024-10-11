package com.example.gymcrm.filter;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Slf4j
@Component
@Order(2)
public class RequsetRsponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logRequestDetails(wrappedRequest);
            logResponseDetails(wrappedResponse);

            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        
        String params = getParams(request);
        String requestBody = getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding());

        log.info("Endpoint Called: method={}, uri={}", method, uri + (queryString != null ? "?" + queryString : ""));
        log.info("Request Body: {}", requestBody);
        log.info("Request Params: {}", params);
    }    

    //print response details only if error occurs
    private void logResponseDetails(ContentCachingResponseWrapper response) throws IOException {
        int status = response.getStatus();

        if (status >= 400) {
            String responseBody = getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
            log.info("Response Status: {}, Response Body: {}", status, responseBody);
        } else {
            log.info("Response Status: {}", status);
        }   
    }

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        try {
            return new String(buf, 0, buf.length, charsetName != null ? charsetName : StandardCharsets.UTF_8.name());
        } catch (Exception ex) {
            return "[unknown]";
        }
    }

    private String getParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            paramsString.append("\n").append(entry.getKey()).append("=").append(String.join(",", entry.getValue()));
        }
        return paramsString.toString();
    }
}
