package com.example.tirociniokelyon.com.example.tirociniokelyon.model

data class Device(
    var id: String = "",
    var key: String = "",
    var softVer: String = "",
    var hardVer: String = "",
    var firmVer: String = "",
    var isConnected: Boolean = false
) {
    fun reset() {
        id = ""
        key = ""
        softVer = ""
        hardVer = ""
        firmVer = ""
        isConnected = false
    }
}