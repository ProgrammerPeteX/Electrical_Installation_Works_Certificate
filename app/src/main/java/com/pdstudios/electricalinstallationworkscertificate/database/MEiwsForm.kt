package com.pdstudios.electricalinstallationworkscertificate.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val tableName = "minor_eiws_form_table"

@Entity(tableName = tableName)
data class MEiwsForm (
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Long = 0L,

    @ColumnInfo
    var detailsOfClient: String = "",

    @ColumnInfo
    var dateMinorWorksCompleted: String = "",

    @ColumnInfo
    var installationAddress: String = "",

    @ColumnInfo
    var descriptionMinorWorks: String = "",

    @ColumnInfo
    var detailsOfDepartures: String = "",

    @ColumnInfo
    var commentsExistingInstallation: String = "",

    @ColumnInfo
    var riskAssessmentAttached: Boolean = false
) {
    companion object {
        val TABLE_NAME = tableName
    }
}
