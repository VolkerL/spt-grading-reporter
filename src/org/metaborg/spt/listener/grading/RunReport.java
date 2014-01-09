package org.metaborg.spt.listener.grading;

public class RunReport {

	private boolean succeeded;
	private String notification;
	private int points;
	
	public RunReport(String notification, int points, boolean succeeded) {
		this.succeeded = succeeded;
		this.notification = notification;
		this.points = points;
	}
	
	public boolean isSuccess() {
		return succeeded;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getNotification() {
		return notification;
	}
}
