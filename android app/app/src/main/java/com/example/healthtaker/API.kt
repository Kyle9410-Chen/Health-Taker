package com.example.healthtaker

import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

object API {
    private const val route = "http://192.168.43.23/api/mobile/"

    fun get(url: String) : String{
        return URL(route + url).readText()
    }

    fun post(url : String, data : String) : String{
        val con = URL(route + url).openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.doOutput = true
        con.addRequestProperty("Content-Type", "application/json")
        con.addRequestProperty("Encoding", "UTF-8")
        DataOutputStream(con.outputStream).use { it.writeBytes(data) }
        return con.inputStream.use { it.bufferedReader().readText() }
    }
}