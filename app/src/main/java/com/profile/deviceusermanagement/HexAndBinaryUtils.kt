package com.profile.deviceusermanagement

import java.util.*

fun hexToBinary(Hex: String?): String? {
    var hex = Hex?.removePrefix("0x")
    var bin = Integer.parseInt(hex, 16).toString(2)
    val inb = bin.toInt()
    bin = String.format(Locale.getDefault(), "%08d", inb)
    return bin
}

// method to convert binary to decimal
fun binaryToDecimal(binary: Long): Int {

    // variable to store the converted
    // binary number
    var binary = binary
    var decimalNumber = 0
    var i = 0

    // loop to extract the digits of the binary
    while (binary > 0) {

        // extracting the digits by getting
        // remainder on dividing by 10 and
        // multiplying by increasing integral
        // powers of 2
        decimalNumber += (Math.pow(2.0, i++.toDouble()) * (binary % 10)).toInt()

        // updating the binary by eliminating
        // the last digit on division by 10
        binary /= 10
    }

    // returning the decimal number
    return decimalNumber
}

// method to convert decimal to hexadecimal
fun binaryToHex(binary: Long): String? {
    // variable to store the output of the
    // binaryToDecimal() method
    val decimalNumber = binaryToDecimal(binary)

    // converting the integer to the desired
    // hex string using toHexString() method
    var hexNumber = Integer.toHexString(decimalNumber)

    // converting the string to uppercase
    // for uniformity
    hexNumber = hexNumber.uppercase(Locale.getDefault())

    // returning the final hex string
    return hexNumber
}

