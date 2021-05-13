package ru.curs.xylophone;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.approvaltests.core.VerifyResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lambda.functions.Function2;

import java.io.File;
import java.io.IOException;

import static bad.robot.excel.matchers.Matchers.sameWorkbook;

public class TestOverall extends FullApprovalsTester {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

	/**
	 * Функция, сравнивающая файлы с содержимым-таблицами.
	 *
	 *  Внутри вызывает matchesSafely нескольких классов в bad.robot.excel.matchers. Это
	 * 		 WorkbookMatcher
	 * 		   SheetsMatcher.hasSameSheetsAs
	 * 		     SheetNumberMatcher.hasSameNumberOfSheetsAs
	 * 		     SheetNameMatcher.containsSameNamedSheetsAs
	 * 		   SheetsMatcher
	 * 		     RowNumberMatcher.hasSameNumberOfRowAs
	 * 		     RowsMatcher.hasSameRowsAs
	 * 		       RowInSheetMatcher.hasSameRow
	 * 		         RowMissingMatcher.rowIsPresent
	 * 		         CellNumberMatcher.hasSameNumberOfCellsAs
	 * 		         CellsMatcher.hasSameCellsAs
	 * 		           CellInRowMatcher.hasSameCell
	 * 		             bad.robot.excel.matchers.CellType.adaptPoi -> bad.robot.excel.cell.Cell
	 * 		             Cell: StringCell->StyledCell
	 * 		             StyledCell.equals = org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals
	 *
	 */
	public Function2<File, File, VerifyResult> compareSpreadsheetFiles = (actualFile, expectedFile) -> {
		try {
			Workbook actual = new XSSFWorkbook(actualFile);
			Workbook expected = new XSSFWorkbook(expectedFile);
			return VerifyResult.from(sameWorkbook(expected).matches(actual));
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
			return VerifyResult.FAILURE;
		}
	};

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
