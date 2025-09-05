package com.paymybuddy.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void info(String message) {
        logger.info("[INFO] - {}", message);
    }

    public void error(String message) {
        logger.error("[ERROR] - {}", message);
    }

    public void error(String message, Throwable throwable) {
        logger.error("[ERROR] - {}", message, throwable);
    }

    public void warn(String message) {
        logger.warn("[WARN] - {}", message);
    }

    public void debug(String message) {
        logger.debug("[DEBUG] - {}", message);
    }

    public void trace(String message) {
        logger.trace("[TRACE] - {}", message);
    }
}
