
package com.example.likebebop.mysound;

/**
 * 
 * @author likebebop
 *
 */

public class StopWatch {

	private long startTimeMillis;
	private long elaspedTimeMillis;

	public StopWatch() {
		start();
	}

	public void start() {
		this.startTimeMillis = System.currentTimeMillis();
	}

	public StopWatch stop() {
		elaspedTimeMillis = System.currentTimeMillis() - startTimeMillis;
		return this;
	}

	public long getElapsedTimeMillis() {
		return elaspedTimeMillis;
	}
}
