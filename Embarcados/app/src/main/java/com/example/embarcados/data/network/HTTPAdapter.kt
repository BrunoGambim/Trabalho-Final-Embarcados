package com.example.embarcados.data.network

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class HTTPAdapter(private var context: Context){
    suspend fun sendPOSTMessage(message: String){
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        var ipComponents = ipAddress.split(".")
        val url = URL("http://${ipComponents[0]}.${ipComponents[1]}.${ipComponents[2]}.1/connect")
        try {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                val wr = OutputStreamWriter(outputStream)
                wr.write(message)
                wr.flush()
                println("Sent 'POST' request to URL : $url; Response Code : $responseCode")
            }
        } catch (e: Exception){
            println(e)
        }
    }
    companion object{
        lateinit var INSTANCE: HTTPAdapter
    }
}