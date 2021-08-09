package com.pdstudios.electricalinstallationworkscertificate

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCurrentDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
    val currentDate = LocalDate.now()
    return currentDate.format(formatter)
}