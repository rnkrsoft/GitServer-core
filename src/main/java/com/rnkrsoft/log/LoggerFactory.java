package com.rnkrsoft.log;

import com.rnkrsoft.log.impl.AndroidLogger;
import com.rnkrsoft.log.impl.StdoutLogger;

public class LoggerFactory {
    static Logger LOGGER = null;

    public static Logger getInstance() {
        if (LOGGER != null) {
            return LOGGER;
        }
        synchronized (LoggerFactory.class) {
            if (LOGGER != null) {
                return LOGGER;
            }
            if (isAndroid()) {
                LOGGER = new AndroidLogger();
            } else {
                LOGGER = new StdoutLogger();
            }
            return LOGGER;
        }

    }

    static boolean isAndroid() {
        String processorArchitecture = System.getenv("processor_architecture");
        return !"AMD64".equals(processorArchitecture);
    }
}
