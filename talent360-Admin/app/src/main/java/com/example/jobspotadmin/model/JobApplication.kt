package com.app.talent360.model

data class JobApplication(
    var jobId: String = "",
    var studentId: String = "",
    var isEvaluated: Boolean = false,
    var applicationStatus: String = "",
    var applicationTimeStamp: String = System.currentTimeMillis().toString()
)
