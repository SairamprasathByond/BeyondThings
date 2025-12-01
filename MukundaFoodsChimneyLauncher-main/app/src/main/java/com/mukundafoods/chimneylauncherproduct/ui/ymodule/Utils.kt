package com.mukundafoods.chimneylauncherproduct.ui.ymodule

object Utils {
    fun writeHandshakePacket() {
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 0x01.toByte()
        var checkSum: Byte = 0
        for (i in 2 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun turnOnOffBulb(value: Int) {
        println("Narayan Bulb Speed $value")

        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 2
        array[2] = value.toByte()
        var checkSum: Byte = 0
        for (i in 2 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun blowerSpeed(value: Int) {
        println("Narayan Blower Speed $value")
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 3
        array[2] = value.toByte()
        var checkSum: Byte = 0
        for (i in 2 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun blowerSpeedForTest(value: Int) {
        println("Narayan Blower Speed test $value")
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 3
        array[2] = value.toByte()
        var checkSum: Byte = 0
        for (i in 2 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun requestUsedTime() {
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 6
        array[2] = 0
        var checkSum: Byte = 0
        for (i in 2 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun initiateCleaning(heatingTime : Int, blowingTime : Int) {
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 7
        array[2] = 0
        array[3] = (heatingTime + blowingTime).toByte()
        array[4] = heatingTime.toByte()
        array[5] = blowingTime.toByte()
        var checkSum: Byte = 0
        for (i in 6 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun stopCleaning() {
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 7
        array[2] = 2
        var checkSum: Byte = 0
        for (i in 3 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }

    fun chimneySettings(cleaningSchedule : Int){
        val array = ByteArray(64)
        array[0] = 0x13.toByte()
        array[1] = 8
        array[2] = 0
        array[3] = 0
        array[4] = 0
        array[5] = 0
        array[6] = cleaningSchedule.toByte()
        var checkSum: Byte = 0
        for (i in 7 until array.size - 2) {
            checkSum = (checkSum + array[i]).toByte()
        }
        array[62] = checkSum
        array[63] = 0x12.toByte()
        USBConnection.getInstance().writeArray(array)
    }
}