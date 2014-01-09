# SPT Grading Reporter

This repository contains an `ITestReporter` which reports the result of SPT test cases in a single markdown file.
If you want to know more about how ITestReporters work with SPT, please read the readme of my [SPT repository](https://github.com/VolkerL/spt).

This `ITestReporter` is called `GradingReporter`.

## Usage

To use this TestReporter you first need to build the jar file using `ant`.
The jar will be created in the `dist` folder.

Now that you have the jar you can add it to the classpath of your sunshine SPT call as described in the readme of my SPT repository. It will then automatically be registered as TestReporter.

## Implementation Notes

### ITestReporter Interface

The TestReporter is updated each time a test case or test suite is added or executed.
SPT reports in the following way:

1. it starts with a `reset` before it starts parsing a test suite (so you get a reset() per test suite)
2. it calls `addTestSuite` to notify you it will start running the test cases in the test suite
3. it calls `addTestCase` to notify you of all test cases in the test suite
4. it calls `startTestCase` to notify you that it started running a testcase
5. it calls `finishTestCase` to notify you of the result of running the test case
6. step 4 and 5 are repeated for each test case in the suite
7. if there are more SPT files to parse, jump back to 1 and start the new test suite

### TestSuite, TestCase and TestCase.Run

These classes represent the SPT test suites and their test cases.
It allows the GradingReporter to keep track of the current state of the test process.

A TestSuite is uniquely identified by its filename.
A TestCase is uniquely identified by its TestSuite and its name (i.e. description).
Therefore a single TestCase can actually represent multiple SPT test cases in the same test suite with the same name.

When you start and finish a TestCase, the statistics are recorded in a `TestCase.Run`.
For each spt test case we expect exactly 1 start and finish call, and so 1 Run.
This means that the executions of similarly named test cases will be recorded as multiple Runs in a single TestCase. This allows the GradingReporter to consider a TestCase as passed, as long as it has a single successful Run. This is useful when testing for errors and for example both 2 errors or 3 errors would be fine. You just specify two test cases with the same name.

### Folder, Category and RunReport

The GradingReporter reports in the following way:

1. it tries to find a single ancestor (folder) of all test suites. This folder is considered the common root folder.
2. any subfolder of this folder is considered a Folder and will get its own markdown section in the report
3. any Folder can contain more Folders and/or Categories
4. a test suite is considered a Category, and the test suite name will be the corresponding markdown section
5. a Category keeps track of RunReports. It tracks 1 report per TestCase and allows you to specify how much points passing each test case is worth. It also allows you to specify a notification which will be shown in the report if the test case failed.
