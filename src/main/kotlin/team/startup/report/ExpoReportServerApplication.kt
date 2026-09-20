package team.startup.report

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExpoReportServerApplication

fun main(args: Array<String>) {
    runApplication<ExpoReportServerApplication>(*args)
}
