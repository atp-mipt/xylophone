package ru.curs.xylophone;

import com.github.miachm.sods.SpreadSheet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.approvaltests.Approvals;
import org.approvaltests.approvers.FileApprover;
import org.approvaltests.core.Options;
import org.approvaltests.core.VerifyResult;
import org.approvaltests.writers.ApprovalBinaryFileWriter;

import java.io.*;

import static bad.robot.excel.matchers.Matchers.sameWorkbook;

public class FullApprovalsTester {
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
    VerifyResult compareSpreadsheetPOIFiles(File actualFile, File expectedFile) {
        try {
            Workbook actual = new XSSFWorkbook(actualFile);
            Workbook expected = new XSSFWorkbook(expectedFile);
            return VerifyResult.from(sameWorkbook(expected).matches(actual));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
            return VerifyResult.FAILURE;
        }
    }

    /**
     * Функция, сравнивающая файлы с содержимым-таблицами для ODS.
     * Внутри вызывает equals для SpreadSheet
     */
    VerifyResult compareSpreadsheetODSFiles(File actualFile, File expectedFile) {
        try {
            SpreadSheet actual = new SpreadSheet(actualFile);
            SpreadSheet expected = new SpreadSheet(expectedFile);
            return VerifyResult.from(actual.equals(expected));
        } catch (IOException e) {
            e.printStackTrace();
            return VerifyResult.FAILURE;
        }
    }

    public void approvalTest(String descriptorPath, String dataPath, String templatePath,
                             OutputType outputType, boolean useSax)
            throws XylophoneError {
        InputStream descrStream = TestReader.class.getResourceAsStream(descriptorPath);
        InputStream dataStream = TestReader.class.getResourceAsStream(dataPath);
        InputStream templateStream = TestReader.class.getResourceAsStream(templatePath);

        // write results to binary buffer
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XML2Spreadsheet.process(dataStream, descrStream, templateStream, outputType, useSax, false, bos);
        byte[] writtenData = bos.toByteArray();

        // verify it
        Options options = new Options();
        Approvals.verify(
                new FileApprover(
                        new ApprovalBinaryFileWriter(new ByteArrayInputStream(writtenData),
                                outputType.getExtension()),
                        options.forFile().getNamer(),
                        (outputType == OutputType.ODS) ? this::compareSpreadsheetODSFiles :
                                this::compareSpreadsheetPOIFiles
                ),
                options
        );
    }
}
