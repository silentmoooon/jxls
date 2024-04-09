package net.sf.jxls;

import org.apache.poi.ss.usermodel.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Leonid Vysochyn
 */
public class CellsChecker   {

    Map propertyMap = new HashMap();

    public CellsChecker() {
    }

    boolean ignoreStyle = false;
    boolean ignoreFirstLastCellNums = false;

    public CellsChecker(Map propertyMap) {
        this.propertyMap = propertyMap;
    }

    public CellsChecker(Map propertyMap, boolean ignoreStyle) {
        this.propertyMap = propertyMap;
        this.ignoreStyle = ignoreStyle;
    }


    public boolean isIgnoreFirstLastCellNums() {
        return ignoreFirstLastCellNums;
    }

    public void setIgnoreFirstLastCellNums(boolean ignoreFirstLastCellNums) {
        this.ignoreFirstLastCellNums = ignoreFirstLastCellNums;
    }

    void checkSection(Sheet srcSheet, Sheet destSheet, int srcRowNum, int destRowNum, short fromCellNum, short toCellNum, int numberOfRows, boolean ignoreHeight, boolean ignoreNullRows) {
        for (int i = 0; i < numberOfRows; i++) {
            Row sourceRow = srcSheet.getRow(srcRowNum + i);
            Row destRow = destSheet.getRow(destRowNum + i);
            if (!ignoreNullRows) {
                assertTrue( (sourceRow != null && destRow != null) || (sourceRow == null && destRow == null),"Null Row problem found");
                if (sourceRow != null) {
                    if (!ignoreHeight) {
                        assertEquals( sourceRow.getHeight(), destRow.getHeight(),"Row height is not the same");
                    }
                    checkCells(sourceRow, destRow, fromCellNum, toCellNum);
                }
            } else {
                if (!ignoreHeight) {
                    assertEquals( sourceRow.getHeight(), destRow.getHeight(),"Row height is not the same");
                }
                if (sourceRow == null && destRow != null) {
                    checkEmptyCells(destRow, fromCellNum, toCellNum);
                }
                if (destRow == null && sourceRow != null) {
                    checkEmptyCells(sourceRow, fromCellNum, toCellNum);
                }
                if (sourceRow != null && destRow != null) {
                    checkCells(sourceRow, destRow, fromCellNum, toCellNum);
                }
            }
        }
    }

    void checkRow(Sheet sheet, int rowNum, int startCellNum, int endCellNum, Object[] values){
        Row row  = sheet.getRow(rowNum);
        if( row != null){
            for(int i = startCellNum; i<=endCellNum; i++){
                Cell cell = row.getCell(i);
                if( cell != null ){
                    Object cellValue = getCellValue(cell, values[i]);
                    assertEquals( values[i], cellValue,"Result cell values incorrect in row=" + row + ", cell=" + i);
                }else{
                    fail("Cell is null");
                }
            }
        }else{
            fail("Row is null");
        }
    }

    void checkCell(Sheet sheet, int rowNum, int cellNum, Object value){
        Row row  = sheet.getRow(rowNum);
        if( row != null){
            Cell cell = row.getCell(cellNum);
            if( cell != null ){
                Object cellValue = getCellValue(cell, value);
                assertEquals( value, cellValue,"Result cell values incorrect in row=" + row + ", cell=" + cellNum);
            }else{
                fail("Cell is null");
            }
        }else{
            fail("Row is null");
        }
    }

    private void checkEmptyCells(Row destRow, short fromCellNum, short toCellNum) {
        if (destRow != null) {
            for (short i = fromCellNum; i <= toCellNum; i++) {
                assertNull( destRow.getCell(i),"Cell " + i + " in " + destRow.getRowNum() + " row is not null");
            }
        }
    }

    void checkListCells(Sheet srcSheet, int srcRowNum, Sheet sheet, int startRowNum, short cellNum, Object[] values) {
        Row srcRow = srcSheet.getRow(srcRowNum);
        Cell srcCell = srcRow.getCell(cellNum);
        for (int i = 0; i < values.length; i++) {
            Row row = sheet.getRow(startRowNum + i);
            Cell cell = row.getCell(cellNum);
            Object cellValue = getCellValue(cell, values[i]);
            assertEquals( values[i], cellValue,"List property cell is incorrect");
            checkCellStyle(srcCell.getCellStyle(), cell.getCellStyle());
        }
    }

    void checkFixedListCells(Sheet srcSheet, int srcRowNum, Sheet destSheet, int startRowNum, short cellNum, Object[] values) {
        for (int i = 0; i < values.length; i++) {
            Row srcRow = srcSheet.getRow(srcRowNum);
            Cell srcCell = srcRow.getCell(cellNum);
            Row destRow = destSheet.getRow(startRowNum + i);
            Cell destCell = destRow.getCell(cellNum);
            Object cellValue = getCellValue(destCell, values[i]);
            assertEquals( values[i], cellValue,"List property cell is incorrect");
            checkCellStyle(srcCell.getCellStyle(), destCell.getCellStyle());
        }
    }

    void checkFormulaCell(Sheet sheet, int rowNum, int cellNum, String formula){
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        assertEquals( cell.getCellType(), CellType.FORMULA,"Result Cell is not a formula");
        assertEquals( formula, cell.getCellFormula(),"Formula is incorrect");
    }

    void checkFormulaCell(Sheet srcSheet, int srcRowNum, Sheet destSheet, int destRowNum, short cellNum, String formula) {
        Row srcRow = srcSheet.getRow(srcRowNum);
        Cell srcCell = srcRow.getCell(cellNum);
        Row destRow = destSheet.getRow(destRowNum);
        Cell destCell = destRow.getCell(cellNum);
        checkCellStyle(srcCell.getCellStyle(), destCell.getCellStyle());
        assertEquals( destCell.getCellType(), CellType.FORMULA,"Result Cell is not a formula");
        assertEquals(formula, destCell.getCellFormula(),"Formula is incorrect");
    }

    void checkFormulaCell(Sheet srcSheet, int srcRowNum, Sheet destSheet, int destRowNum, short cellNum, String formula, boolean ignoreCellStyle) {
        Row srcRow = srcSheet.getRow(srcRowNum);
        Cell srcCell = srcRow.getCell(cellNum);
        Row destRow = destSheet.getRow(destRowNum);
        Cell destCell = destRow.getCell(cellNum);
        if (!ignoreCellStyle) {
            checkCellStyle(srcCell.getCellStyle(), destCell.getCellStyle());
        }
        assertEquals( destCell.getCellType(), CellType.FORMULA,"Result Cell is not a formula");
        assertEquals( formula, destCell.getCellFormula(),"Formula is incorrect");
    }

    void checkRows(Sheet sourceSheet, Sheet destSheet, int sourceRowNum, int destRowNum, int numberOfRows, boolean checkRowHeight) {
        for (int i = 0; i < numberOfRows; i++) {
            Row sourceRow = sourceSheet.getRow(sourceRowNum + i);
            Row destRow = destSheet.getRow(destRowNum + i);
            assertTrue( (sourceRow != null && destRow != null) || (sourceRow == null && destRow == null),"Null Row problem found");
            if (sourceRow != null && destRow != null) {
                if (!ignoreFirstLastCellNums) {
                    assertEquals( sourceRow.getFirstCellNum(), destRow.getFirstCellNum(),"First Cell Numbers differ in source and result row");
                }
                assertEquals( sourceRow.getPhysicalNumberOfCells(), destRow.getPhysicalNumberOfCells(),"Physical Number Of Cells differ in source and result row");
                if( checkRowHeight ){
                    assertEquals(
                            sourceRow.getHeight(), destRow.getHeight(),"Row height is not the same for srcRow = " + sourceRow.getRowNum() + ", destRow = " + destRow.getRowNum());
                }
                checkCells(sourceRow, destRow, sourceRow.getFirstCellNum(), sourceRow.getLastCellNum());
            }
        }
    }

    private void checkCells(Row sourceRow, Row resultRow, short startCell, short endCell) {
		 if (startCell >= 0 && endCell >= 0) {
			  for (short i = startCell; i <= endCell; i++) {
					Cell sourceCell = sourceRow.getCell(i);
					Cell resultCell = resultRow.getCell(i);
					assertTrue( (sourceCell != null && resultCell != null) || (sourceCell == null && resultCell == null),"Null cell problem found");
					if (sourceCell != null) {
						 checkCells(sourceCell, resultCell);
					}
			  }
		 }
    }

    void checkCells(Sheet srcSheet, Sheet destSheet, int srcRowNum, short srcCellNum, int destRowNum, short destCellNum, boolean checkCellWidth) {
        Row srcRow = srcSheet.getRow(srcRowNum);
        Row destRow = destSheet.getRow(destRowNum);
        assertEquals( srcRow.getHeight(), destRow.getHeight(),"Row height is not the same");
        Cell srcCell = srcRow.getCell(srcCellNum);
        Cell destCell = destRow.getCell(destCellNum);
        assertTrue( (srcCell != null && destCell != null) || (srcCell == null && destCell == null),"Null cell problem found");
        if (srcCell != null && destCell != null) {
            checkCells(srcCell, destCell);
        }
        if (checkCellWidth) {
            assertEquals( getWidth(srcSheet, srcCellNum), getWidth(destSheet, destCellNum),"Cell Widths are different");
        }
    }

    static int getWidth(Sheet sheet, int col) {
        int width = sheet.getColumnWidth(col);
        if (width == sheet.getDefaultColumnWidth()) {
            width = (short) (width * 256);
        }
        return width;
    }


    private void checkCells(Cell sourceCell, Cell destCell) {
        checkCellValue(sourceCell, destCell);
        checkCellStyle(sourceCell.getCellStyle(), destCell.getCellStyle());
    }

    private void checkCellStyle(CellStyle sourceStyle, CellStyle destStyle) {
        if (!ignoreStyle) {
            assertEquals(sourceStyle.getAlignment(), destStyle.getAlignment());
            assertEquals(sourceStyle.getBorderBottom(), destStyle.getBorderBottom());
            assertEquals(sourceStyle.getBorderLeft(), destStyle.getBorderLeft());
            assertEquals(sourceStyle.getBorderRight(), destStyle.getBorderRight());
            assertEquals(sourceStyle.getBorderTop(), destStyle.getBorderTop());
            assertEquals(sourceStyle.getBottomBorderColor(), sourceStyle.getBottomBorderColor());
            assertEquals(sourceStyle.getFillBackgroundColor(), destStyle.getFillBackgroundColor());
            assertEquals(sourceStyle.getFillForegroundColor(), sourceStyle.getFillForegroundColor());
            assertEquals(sourceStyle.getFillPattern(), destStyle.getFillPattern());
            assertEquals(sourceStyle.getHidden(), destStyle.getHidden());
            assertEquals(sourceStyle.getIndention(), destStyle.getIndention());
            assertEquals(sourceStyle.getLeftBorderColor(), destStyle.getLeftBorderColor());
            assertEquals(sourceStyle.getLocked(), destStyle.getLocked());
            assertEquals(sourceStyle.getRightBorderColor(), destStyle.getRightBorderColor());
            assertEquals(sourceStyle.getRotation(), destStyle.getRotation());
            assertEquals(sourceStyle.getTopBorderColor(), destStyle.getTopBorderColor());
            assertEquals(sourceStyle.getVerticalAlignment(), destStyle.getVerticalAlignment());
            assertEquals(sourceStyle.getWrapText(), destStyle.getWrapText());
        }
    }

    private void checkCellValue(Cell sourceCell, Cell destCell) {
        switch (sourceCell.getCellType()) {
            case CellType.STRING:
                if (propertyMap.containsKey(sourceCell.getRichStringCellValue().getString())) {
                    assertEquals( propertyMap.get(sourceCell.getRichStringCellValue().getString()), getCellValue(destCell, propertyMap.get(sourceCell.getRichStringCellValue().getString())),"Property value was set incorrectly");
                } else {
                    assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                    assertEquals( sourceCell.getRichStringCellValue().getString(), destCell.getRichStringCellValue().getString(),"Cell values are not the same");
                }
                break;
            case CellType.NUMERIC:
                assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                assertTrue( sourceCell.getNumericCellValue() == destCell.getNumericCellValue(),"Cell values are not the same");
                break;
            case CellType.BOOLEAN:
                assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                assertEquals( sourceCell.getBooleanCellValue(), destCell.getBooleanCellValue(),"Cell values are not the same");
                break;
            case CellType.ERROR:
                assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                assertEquals( sourceCell.getErrorCellValue(), destCell.getErrorCellValue(),"Cell values are not the same");
                break;
            case CellType.FORMULA:
                assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                assertEquals( sourceCell.getCellFormula(), destCell.getCellFormula(),"Cell values are not the same");
                break;
            case CellType.BLANK:
                assertEquals( sourceCell.getCellType(), destCell.getCellType(),"Cell type is not the same");
                break;
            default:
                fail("Unknown cell type, code=" + sourceCell.getCellType() + ", value=" + sourceCell.getRichStringCellValue().getString());
                break;
        }
    }

    private Object getCellValue(Cell cell, Object obj) {
        Object value = null;
        if (obj instanceof String) {
            value = cell.getRichStringCellValue().getString();
        } else if (obj instanceof Double) {
            value = cell.getNumericCellValue();
        } else if (obj instanceof BigDecimal) {
            value = new BigDecimal(Double.toString(cell.getNumericCellValue()));
        } else if (obj instanceof Integer) {
            value = (int) cell.getNumericCellValue();
        } else if (obj instanceof Float) {
            value = (float)(cell.getNumericCellValue());
        } else if (obj instanceof Date) {
            value = cell.getDateCellValue();
        } else if (obj instanceof Calendar) {
            Calendar c = Calendar.getInstance();
            c.setTime(cell.getDateCellValue());
            value = c;
        } else if (obj instanceof Boolean) {
            if (cell.getCellType() == CellType.BOOLEAN) {
                value = (cell.getBooleanCellValue()) ? Boolean.TRUE : Boolean.FALSE;
            } else if (cell.getCellType() == CellType.STRING) {
                value = Boolean.valueOf(cell.getRichStringCellValue().getString());
            } else {
                value = Boolean.FALSE;
            }
        }
        return value;
    }

}
