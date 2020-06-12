package com.oxagile.itapp.device


data class Device(
    val brand: String, // бренд устройства (Xiaomi)
    val os: String = "Android", // операционная система (Android)
    val os_version: String, // версия ОС (10)
    val device_model: String, // модель устройства (Redmi 8)
    val device_type: String = "PHONE",
    val device_id: String, // уникальный id,
    val push_token: String = ""
)
