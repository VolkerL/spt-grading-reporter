package org.metaborg.spt.listener.grading;

import java.util.HashMap;
import java.util.Map;

public class Folder extends Reportable {
	private String name;
	private Map<String, Reportable> children;
	
	public Folder(String name) {
		this.name = name;
		this.children = new HashMap<>();
	}
	
	public void addChild(Reportable child) {
		children.put(child.getName(), child);
	}
	
	public Reportable getChild(String name) {
		return children.get(name);
	}
	
	@Override
	public String toMarkDown(int nestedDepth) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < nestedDepth; i++) {
			builder.append('#');
		}
		builder.append(' ').append(name).append("\n\n");
		for (Reportable child : children.values()) {
			builder.append(child.toMarkDown(nestedDepth + 1)).append('\n');
		}
		return builder.toString();
	}
	
	public String getName() {
		return name;
	}
}
