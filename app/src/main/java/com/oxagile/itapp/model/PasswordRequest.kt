package com.oxagile.itapp.model

import com.google.gson.annotations.SerializedName

data class PasswordRequest (

	@field:SerializedName("device")
	val device: DevicePassword
)

data class DevicePassword (

	@field:SerializedName("device_password")
	val devicePassword: String
)
