
package com.example.likebebop.mysound;


/**
 * 
 * @author likebebop
 *
 */
public class HandyProfiler {
	StopWatch stopWatch = new StopWatch();
	KaleLog logger;
	String message;
	LogLevel level;

	public static enum LogLevel {
		DEBUG,
		INFO
	}

	public HandyProfiler(KaleLog logger) {
		this.logger = logger;
		tick();
	}

	public HandyProfiler(KaleLog logger, LogLevel level) {
		this.logger = logger;
		this.level = level;
		tick();
	}

	public void tick(String message) {
		this.message = message;
		if (LogLevel.DEBUG.equals(level)) {
			logger.d(message);
		} else {
			logger.i(message);
		}
		tick();
	}

	public void tick(String message, LogLevel level) {
		this.level = level;
		this.message = message;
		tick();
	}

	public void tick() {
		stopWatch.start();
	}

	private String getMessage(String message) {
		return "[" + stopWatch.getElapsedTimeMillis() + " ms], " + message;
	}

	public void tockWithInfo(String message) {
		stopWatch.stop();

		if (KaleConfig.INSTANCE.logging()) {
			logger.i(getMessage(message));
		}

	}

	public void tockWithDebug(String message) {
		stopWatch.stop();

		logger.d(getMessage(message));

	}

	public void tock() {
		stopWatch.stop();
		if (LogLevel.DEBUG.equals(level)) {
			logger.d(getMessage(message));
		} else {
			logger.i(getMessage(message));
		}
	}
}
