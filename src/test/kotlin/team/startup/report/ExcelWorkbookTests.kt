package team.startup.report

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ExcelWorkbookTests :
    StringSpec({
        "엑셀 파일을 저장하고 다시 읽을 수 있다" {
            val bytes =
                ByteArrayOutputStream().use { output ->
                    XSSFWorkbook().use { workbook ->
                        workbook
                            .createSheet("보고서")
                            .createRow(0)
                            .createCell(0)
                            .setCellValue("박람회")
                        workbook.write(output)
                    }
                    output.toByteArray()
                }
            XSSFWorkbook(ByteArrayInputStream(bytes)).use { workbook ->
                workbook
                    .getSheet("보고서")
                    .getRow(0)
                    .getCell(0)
                    .stringCellValue shouldBe "박람회"
            }
        }
    })
