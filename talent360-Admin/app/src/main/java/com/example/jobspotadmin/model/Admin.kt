package com.app.talent360.model

import com.app.talent360.util.Constants.Companion.ROLE_TYPE_ADMIN

data class Admin(
    var uid : String = "",
    var email : String = "",
    var username : String = "",
    var imageUrl : String = "",
    val roleType : String = ROLE_TYPE_ADMIN
)