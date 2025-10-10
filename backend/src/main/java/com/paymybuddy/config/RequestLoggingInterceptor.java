package com.paymybuddy.config;

import com.paymybuddy.logging.LoggingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private final LoggingService loggingService;

    public RequestLoggingInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        LocalDateTime startTime = LocalDateTime.now();
        request.setAttribute("startTime", startTime);

        loggingService.info(String.format("[PAYMYBUDDY] - REQUEST: %s %s - IP: %s",
                request.getMethod(),
                request.getRequestURI(),
                getClientIpAddress(request)));

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) {
        LocalDateTime startTime = (LocalDateTime) request.getAttribute("startTime");
        LocalDateTime endTime = LocalDateTime.now();
        long duration = ChronoUnit.MILLIS.between(startTime, endTime);

        String status = String.valueOf(response.getStatus());

        // Get authenticated user from SecurityContext
        String userInfo = "anonymous";
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            userInfo = authentication.getName();
        }

        loggingService.info(String.format("[PAYMYBUDDY] - RESPONSE: %s %s - User: %s - Status: %s - Duration: %dms",
                request.getMethod(),
                request.getRequestURI(),
                userInfo,
                status,
                duration));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
