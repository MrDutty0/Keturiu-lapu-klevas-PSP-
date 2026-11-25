package com.example

import io.ktor.server.application.Application
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.ApplicationStopped
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.apache.commons.csv.CSVFormat
import java.io.StringReader

@Volatile
var current_buses: List<Bus> = emptyList()


suspend fun getBusLocations(client: HttpClient): String {
    val response: HttpResponse = client.get("https://m.stops.lt/vilnius/gps.txt")
    val body: String = response.bodyAsText()

    return body
}


fun parseBusCSV(busCSV: String): List<Bus> {
    val format = CSVFormat.DEFAULT.builder()
        .setHeader("direction", "route", "lon", "lat", "a", "b", "id_and_flags", "-1", "45").build()

    val records = format.parse(StringReader(busCSV)).use { it.records }

    val buses: List<Bus> = records.map { r ->
        val idAndFlags = r["id_and_flags"].split(" ")
        val flags = idAndFlags.getOrElse(1, { "" })

        Bus(
            direction = r["direction"] != "0",
            route = r["route"],
            lon = r["lon"].toDouble(),
            lat = r["lat"].toDouble(),
            a = r["a"],
            b = r["b"],
            id = idAndFlags.first(),
            K = flags.contains("K"),
            W = flags.contains("W"),
            N = flags.contains("N"),
            Z = flags.contains("Z"),
            D = flags.contains("D"),
        )
    }

    return buses
}


fun Application.startPolling() {
    val client = HttpClient(CIO)
    monitor.subscribe(ApplicationStopped) { client.close() }

    launch {
        while (isActive) {
            try {
                val busCSV: String = getBusLocations(client)
                current_buses = parseBusCSV(busCSV)
            } catch (e: CancellationException) {
                throw(e)
            }
            catch (e: Exception) {
                println("Polling failed$e")
            }
            delay(6000)
        }
    }
}