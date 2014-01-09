package org.metaborg.spt.listener.grading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of an SPT testsuite.
 * 
 * @author Volker Lanting
 *
 */
public class TestSuite {

	private String name;
	private String fileName;
	private Map<String, TestCase> tests;
	
	public TestSuite() {
		this(null, null);
	}
	
	public TestSuite(String name, String fileName) {
		this.name = name;
		this.fileName =fileName;
		tests = new HashMap<>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Collection<TestCase> getTests() {
		return tests.values();
	}
	public void setTests(Collection<TestCase> tests) {
		this.tests.clear();
		for (TestCase test : tests) {
			addTest(test);
		}
	}
	public void addTest(TestCase test) {
		if (tests.containsKey(test.getName())) {
			tests.get(test).incCount();
		} else {
			tests.put(test.getName(), test);
		}
	}
	
	/**
	 * Get the test case with the given description.
	 * @param description the name of the test cases you want.
	 * @return the test case. Can be null.
	 */
	public TestCase getTest(String description) {
		return tests.get(description);
	}
}
