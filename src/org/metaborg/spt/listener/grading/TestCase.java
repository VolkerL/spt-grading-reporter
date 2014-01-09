package org.metaborg.spt.listener.grading;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A representation of an SPT test case.
 * 
 * @author VolkerLanting
 *
 */
public class TestCase {

	private String name;
	private long start;
	private long end;
	private Collection<Run> runs;
	private int count;
	
	public TestCase() {
		this(null);
	}
	
	public TestCase(String name) {
		this.name = name;
		this.count = 1;
		this.runs = new ArrayList<Run>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCount() {
		return count;
	}
	public void incCount() {
		count++;
	}
	
	
	/**
	 * Provides the information of all runs of this test case.
	 * @return the runs
	 */
	public Collection<Run> getRuns() {
		return runs;
	}
	
	public boolean isFinished() {
		return runs.size() >= count;
	}
	
	/**
	 * Notify the test case that its execution has finished.
	 * @param result the result of the test execution. Should be true for success, false for failure.
	 * @throws IllegalArgumentException if the test case was never started.
	 */
	public void finish(boolean result, Collection<String> messages) throws IllegalArgumentException {
		if (start == -1) {
			throw new IllegalArgumentException("This testcase was never started");
		} else {
			end = System.currentTimeMillis();
			runs.add(new Run(end - start, result, messages));
			start = -1;
			end = -1;
		}
	}
	
	/**
	 * Notify the test case that its execution has started.
	 */
	public void start() {
		end = -1;
		start = System.currentTimeMillis();
	}
	
	public static class Run {
		private long runtime;
		private boolean result;
		private Collection<String> messages;
		
		private Run(long runtime, boolean result, Collection<String> messages) {
			this.runtime = runtime;
			this.result = result;
			this.messages = messages;
		}
		
		public long getRuntime() {
			return runtime;
		}
		public boolean passed() {
			return result;
		}
		public Collection<String> getMessages() {
			return messages;
		}
	}
}
