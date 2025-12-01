package com.mukundafoods.chimneylauncherproduct.ui.chimney

sealed class CheckSerialNumber {

    object Success : CheckSerialNumber()

    object Failure : CheckSerialNumber()

    object NoInternet : CheckSerialNumber()
}