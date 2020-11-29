package raziel23x.projectskyblock.utils;

import org.apache.logging.log4j.Logger;

public class ProjectSkyblockLogger {

    private final Logger LOGGER;

    public ProjectSkyblockLogger(Logger logger) {
        LOGGER = logger;
    }

    public void info(String msg) {
        LOGGER.info(addPrefix(msg));
    }

    public void warn(String msg) {
        LOGGER.warn(addPrefix(msg));
    }

    public void error(String msg) {
        LOGGER.error(addPrefix(msg));
    }

    private String addPrefix(String msg) {
        return "[Project Skyblock]: " + msg;
    }

    public void info(int num) {
        info(String.format("%d", num));
    }
}
