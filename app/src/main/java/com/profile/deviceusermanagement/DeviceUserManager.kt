package com.profile.deviceusermanagement

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class DeviceUserManager(val contex: Context, val listener: UpdateListener) {

    private val TAG: String = "DeviceUserManager"
    private lateinit var deviceUserList: ArrayList<UserData>
    private lateinit var portalUserList: ArrayList<UserData>
    private lateinit var updatedUserList: ArrayList<UserData>
    private var currentDeviceId = 0
    suspend fun readPortalUser(): ArrayList<UserData> {
        val readJob = CoroutineScope(Dispatchers.IO).async {
            readFile("PortalUserList.txt")
        }
        portalUserList = readJob.await()

        return portalUserList
    }

    fun readDeviceUser() {

        CoroutineScope(Dispatchers.IO).launch {
            val users = async { readFile("DeviceUserList.txt") }

            deviceUserList = users.await()
            listener.deviceUserUpdate(deviceUserList)
            Log.e(TAG, "readDeviceUser: ${deviceUserList.size}", )
        }

//        deviceUserList = readFile("DeviceUserList.txt")

    }

    /**
     *To read file from assets
     */
    private fun readFile(fileName: String): ArrayList<UserData> {
        var userList: ArrayList<UserData> = ArrayList<UserData>()
        try {
            val reader =
                BufferedReader(InputStreamReader(contex.getAssets().open(fileName)))

            var line: String? = null;
            while ({ line = reader.readLine(); line }() != null) {
                line?.let { Log.e("code", it) }
                val rowData = line?.split("\t")

                if (currentDeviceId == 0) {
                    currentDeviceId = rowData?.get(1)?.toInt() ?: 0
                }
//                Log.e(TAG, "UseArray: " + rowData)

                var userData: UserData = UserData(rowData!![0], rowData[1].toInt(), rowData[2])
                if (currentDeviceId != 0 && userData.devieId == currentDeviceId) {
                    userList.add(userData)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return userList
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun writeModifiedFile() {
//        DeviceUserListUpdated.txt
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val currentDateTime = LocalDateTime.now().format(formatter)
        val baseDir = Environment.getExternalStorageDirectory().absolutePath
        val fileName: String = "DeviceUserListUpdated_$currentDateTime.txt"

        val filePath = File("$baseDir/DeviseUsers")
        var success = true
        if (!filePath.exists())
            try {
                success = filePath.mkdir()
            } catch (e: Exception) {
                e.printStackTrace()
            }


        if (success) {
            val file = File(filePath.toString() + File.separator + fileName)

            try {
                val fOut = FileOutputStream(file)
                val myOutWriter = OutputStreamWriter(fOut)
                for (deviceUser in updatedUserList) {
                    var trained = 0x00
                    var binary = hexToBinary(deviceUser.userStatus)
                    var value = binary?.get(1)?.digitToInt()
                    if (value == 1) {
                        trained = 0x01
                    }

//TODO:: Below statement can be utilize to remove 6th bit of binary which use for trained or untrained status
//                    binary= binary?.removeRange(1,2) //for now hex value remain same
                    var hexValue = binary?.let { binaryToHex(it.toLong()) }
                    myOutWriter.append("${deviceUser.userId}\t${deviceUser.devieId}\t0X${hexValue}\t0X0$trained")
                    myOutWriter.append("\n\r")
                }
                myOutWriter.close()
                fOut.close()
            } catch (e: Exception) {
                // handle the exception
                e.printStackTrace()
            }

        } else {
            // directory creation is not successful
            Handler(Looper.getMainLooper()).post(){
                Toast.makeText(
                    contex,
                    "Directory does not exist. Please allow all the permission!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }


    suspend fun updateUsers() {
        var jobPortalUser = CoroutineScope(Dispatchers.IO).launch {
            readPortalUser()
        }
        jobPortalUser.join()
        updatedUserList = arrayListOf()

        for (deviceUser in deviceUserList) {
            var fetchedData = portalUserList.singleOrNull { it.userId == deviceUser.userId }
            if (fetchedData != null) {
                if (hexToBinary(deviceUser.userStatus) != hexToBinary(fetchedData.userStatus)) {
                    deviceUser.userStatus = fetchedData.userStatus
                }
            }
            updatedUserList.add(deviceUser)
        }

        for (portalUser in portalUserList) {
            var dataToAdd = updatedUserList.singleOrNull { it.userId == portalUser.userId }
            if (dataToAdd == null) {
                updatedUserList.add(portalUser)
            }
        }
       
        val job = CoroutineScope(Dispatchers.IO).launch {
            writeModifiedFile()
        }
        job.join()
        listener.deviceUserUpdate(updatedUserList)
        Log.e(TAG, "updateUsers: ${updatedUserList.size}", )

    }
}