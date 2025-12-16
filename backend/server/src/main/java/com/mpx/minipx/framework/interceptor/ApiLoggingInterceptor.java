package com.mpx.minipx.framework.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);

        // 컨트롤러/메서드 정보까지 로그에 찍기
        if (handler instanceof HandlerMethod handlerMethod) {
            String controller = handlerMethod.getBeanType().getSimpleName();
            String handlerMethodName = handlerMethod.getMethod().getName();

            log.info("[API-REQ] {} {} -> {}.{} (clientIp={})",
                    method, uri, controller, handlerMethodName, clientIp);
        } else {
            log.info("[API-REQ] {} {} (clientIp={})", method, uri, clientIp);
        }

        // 처리 시간 측정용
        request.setAttribute("startTime", System.currentTimeMillis());
        return true; // 계속 진행
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[API-RES] {} {} status={} elapsed={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsed);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim(); // Nginx 뒤에 있을 때
        }
        return request.getRemoteAddr();
    }
}