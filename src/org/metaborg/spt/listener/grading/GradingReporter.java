package org.metaborg.spt.listener.grading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.metaborg.spt.listener.ITestReporter;
import org.metaborg.spt.listener.grading.TestCase.Run;

public class GradingReporter implements ITestReporter{

	// an index from test suite filename to test suite
	private Map<String, TestSuite> suites;
	
	public GradingReporter() {
		this.suites = new HashMap<String, TestSuite>();
	}
	
	@Override
	public void addTestcase(String testsuiteFile, String description)
			throws Exception {
		if (suites.containsKey(testsuiteFile)) {
			TestSuite suite = suites.get(testsuiteFile);
			suite.addTest(new TestCase(description));
		} else {
			throw new IllegalArgumentException("This test suite is unknown.");
		}
	}

	@Override
	public void reset() throws Exception {
		// for now do nothing, as we want to gather ALL test suites, not just 1 and reset
//		this.suites = new HashMap<String, TestSuite>();
	}

	@Override
	public void addTestsuite(String name, String filename) throws Exception {
		if (suites.containsKey(filename)) {
			throw new IllegalArgumentException("This test suite exists already.");
		}
		suites.put(filename, new TestSuite(name, filename));
	}

	@Override
	public void startTestcase(String testsuiteFile, String description)
			throws Exception {
		if (suites.containsKey(testsuiteFile)) {
			TestSuite suite = suites.get(testsuiteFile);
			TestCase test = suite.getTest(description);
			if (test == null) {
				throw new IllegalArgumentException("No such test case found.");
			} else {
				test.start();
			}
		} else {
			throw new IllegalArgumentException("No such testsuite exists");
		}
	}

	@Override
	public void finishTestcase(String testsuiteFile, String description,
			boolean succeeded, Collection<String> messages) throws Exception {
		if (suites.containsKey(testsuiteFile)) {
			TestSuite suite = suites.get(testsuiteFile);
			TestCase test = suite.getTest(description);
			if (test == null) {
				throw new IllegalArgumentException("No such test case found.");
			} else {
				test.finish(succeeded, messages);
				checkDone();
			}
		} else {
			throw new IllegalArgumentException("No such testsuite exists");
		}
	}

	// check if all test cases are finished, if so do the reporting
	private void checkDone() {
		for (TestSuite suite : suites.values()) {
			for (TestCase test : suite.getTests()) {
				if (!test.isFinished()) {
					return;
				}
			}
		}
		// all registered tests are done
		report();
	}

	// FIXME make proper reporting to markdown
	/*
	 * Assumes spt files are located in a folder structure, with a common parent folder.
	 * The common parent of all spt files will be considered root, 
	 * from there on out all subfolders are considered to be categories
	 * and finally the suite filename itself is considered a category.
	 */
	private void report() {
		Map<String, Folder> folders = new HashMap<>();
		Path common = getCommonPath();
		for (TestSuite suite : suites.values()) {
			Path suitePath = FileSystems.getDefault().getPath(suite.getFileName());
			Path relative = common.relativize(suitePath);
			Folder folder = null;
			for (int i = 0; i < relative.getNameCount() - 1; i++) {
				String name = relative.getName(i).toString();
				if (folder == null) {
					folder = folders.get(name);
					if (folder == null) {
						folder = new Folder(name);
						folders.put(name, folder);
					}
				} else {
					Reportable rep = folder.getChild(name);
					Folder temp;
					if (rep == null || !(rep instanceof Folder)) {
						temp = new Folder(name);
					} else {
						temp = (Folder) rep;
					}
					folder.addChild(temp);
					folder = temp;
				}
			}
			// if there is no nested folder structure for this test suite, make it artificially
			if (folder == null) {
				String folderName = "Automatic Feedback";
				folder = folders.get(folderName);
				folder = folder == null ? new Folder(folderName) : folder;
				folders.put(folder.getName(), folder);
			}
			Category category = new Category(FilenameUtils.removeExtension(relative.getFileName().toString()));
			for (TestCase test : suite.getTests()) {
				// FIXME make the points configurable
				boolean success = false;
				// a pass of a single test case of multiple same named test cases is enough to make the test case pass
				for (Run run : test.getRuns()) {
					if (run.passed()) {
						success = true;
					}
				}
				category.addReport(new RunReport(test.getName(), 1, success));
			}
			folder.addChild(category);
		}
		
		try {
			// FIXME make configurable
			File out = common.resolve("output.txt").toFile();
			FileWriter writer = new FileWriter(out);
			for (Folder f : folders.values()) {
				writer.write(f.toMarkDown());
				writer.flush();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// get the largest common path of all test suites
	private Path getCommonPath() {
		FileSystem fs = FileSystems.getDefault();
		Path common = null;
		for (TestSuite suite : suites.values()) {
			Path suitePath = fs.getPath(suite.getFileName());
			if (common == null) {
				common = suitePath.subpath(0, suitePath.getNameCount() - 1);
			} else {
				Path temp = suitePath.getRoot();
				for (int i = 0; i < Math.min(suitePath.getNameCount(), common.getNameCount()); i++) {
					if (common.getName(i).equals(suitePath.getName(i))) {
						temp = temp.resolve(common.getName(i));
					} else {
						break;
					}
				}
				common = temp;
			}
		}
		return common;
	}
}
