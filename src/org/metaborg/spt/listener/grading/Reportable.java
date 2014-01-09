package org.metaborg.spt.listener.grading;

public abstract class Reportable {

	public abstract String getName();
	
	public abstract String toMarkDown(int nestedDepth);
	public String toMarkDown() {
		return toMarkDown(1);
	}
}
