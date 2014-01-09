package org.metaborg.spt.listener.grading;

import java.util.ArrayList;
import java.util.Collection;

public class Category extends Reportable {

	private String name;
	private int maxPoints;
	private int points;
	private Collection<RunReport> failures;
	private Collection<RunReport> successes;
	
	public Category(String name) {
		this.name = name;
		this.failures = new ArrayList<>();
		this.successes = new ArrayList<>();
	}
	
	public void addReport(RunReport report) {
		if (report.isSuccess()) {
			successes.add(report);
			points += report.getPoints();
		} else {
			failures.add(report);
		}
		maxPoints += report.getPoints();
	}
	
	/**
	 * Get a MarkDown representation of this Category.
	 * @param nestedDepth the nested depth of this Category.
	 * Starts at 1 for Categories with no parent Folders.
	 * @return the MarkDown representation.
	 */
	@Override
	public String toMarkDown(int nestedDepth) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nestedDepth; i++) {
			builder.append('#');
		}
		builder.append(' ').append(name).append("\n\n");
		builder.append("You scored ").append(points).append(" out of ").append(maxPoints).append(" points.\n\n");
		for (RunReport failure : failures) {
			builder.append("- ").append(failure.getNotification()).append('\n');
		}
		return builder.toString();
	}
	
	@Override
	public String getName() {
		return name;
	}
}
