package team.startup.report.support

import io.kotest.matchers.shouldBe
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle

// XLSX를 ZIP 바이트가 아니라 셀 값·서식 의미로 비교하는 도우미

fun Sheet.cell(
    row: Int,
    column: Int,
) = getRow(row).getCell(column) as XSSFCell

/** 행별 셀 값. 빈 셀(BLANK)은 null, 숫자는 Double 문자열 */
fun Sheet.values(): List<List<String?>> =
    map { row ->
        (0 until row.lastCellNum).map { column ->
            val cell = row.getCell(column)
            when (cell?.cellType) {
                null, CellType.BLANK -> null
                CellType.NUMERIC -> cell.numericCellValue.toString()
                else -> cell.stringCellValue
            }
        }
    }

fun XSSFCellStyle.rgbFill() = fillForegroundXSSFColor?.argbHex?.takeLast(6)

fun XSSFCellStyle.assertThinBorders() {
    listOf(borderTop, borderBottom, borderLeft, borderRight).forEach { it shouldBe BorderStyle.THIN }
}

fun XSSFCell.assertDarkHeader() {
    val style = getCellStyle()
    style.rgbFill() shouldBe "222529"
    style.fillPattern shouldBe FillPatternType.SOLID_FOREGROUND
    style.font.bold shouldBe true
    style.font.xssfColor.argbHex
        .takeLast(6) shouldBe "FFFFFF"
    style.assertThinBorders()
}
