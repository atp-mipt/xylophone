package ru.curs.xylophone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestOverall extends FullApprovalsTester {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void test1() throws XylophoneError {
		approvalTest("descriptor_testdata/overall/testdescriptor3.yaml", "testdata.xml", "template.xlsx",
				OutputType.XLSX, false);
	}

	@Test
	public void test2() throws XylophoneError {
		approvalTest("descriptor_testdata/overall/testsaxdescriptor3.yaml", "testdata.xml", "template.xlsx",
				OutputType.XLSX, true);
	}

}
