package com.example.embarcados.data.network

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SocketAdapter(private var context: Context) {
    suspend fun sendMessage(message: String){
        try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
            var ipComponents = ipAddress.split(".")
            var packet = DatagramPacket(
                message.toByteArray(Charsets.US_ASCII),
                message.length,
                InetAddress.getByName(ipComponents[0] + "." + ipComponents[1] + "." + ipComponents[2] + ".255"),
                4210
            )
            getSocket()!!.send(packet)
            Thread.sleep(10)
            getSocket()!!.send(packet)
            Thread.sleep(10)
            getSocket()!!.send(packet)
            Thread.sleep(10)
            getSocket()!!.send(packet)
            Thread.sleep(10)
            getSocket()!!.send(packet)
        } catch (e: Exception){
            if(getSocket() != null){
                try {
                    socket!!.close()
                } catch (e: Exception){}
                socket = null
            }
        }
    }

    suspend fun receiveMessage(): String{
        try {
            val buffer = ByteArray(2048)
            var packet = DatagramPacket(buffer, buffer.size)
            getSocket()!!.receive(packet)
            return packet.data.decodeToString()
        } catch (e: Exception){
            Thread.sleep(500)
            if(getSocket() != null){
                try {
                    socket!!.close()
                } catch (e: Exception){}
                socket = null
            }
            return ""
        }
    }

    companion object{
        lateinit var INSTANCE: SocketAdapter
        private var socket: DatagramSocket? = null
        private fun getSocket(): DatagramSocket?{
            if(socket == null){
                try{
                    socket = DatagramSocket(4210)
                } catch (e: Exception){}
            }
            return socket
        }
    }
}