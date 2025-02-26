package com.app.talent360.model

data class MockQuestion(
    var question: String = "",
    var options: List<String> = emptyList(),
    var correctOption: String = "",
    var feedback: String = ""
)
