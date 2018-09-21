package org.strykeforce.motion

import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import mu.KotlinLogging

const val POST = "http://192.168.3.208:5000/load"

@JsonClass(generateAdapter = true)
data class Activity(
    val name: String,
    val activityMeasures: List<String> = listOf("profile_ticks", "actual_ticks", "actual_distance"),
    val activityData: MutableList<Double> = mutableListOf(),
    val traceMeasures: List<String> = listOf(
        // millis stripped out by loader
        "profile_acc",
        "profile_vel",
        "setpoint_vel",
        "actual_vel",
        "profile_ticks",
        "actual_ticks",
        "foward",
        "strafe",
        "azimuth"
    ),
    val traceData: MutableList<List<Double>> = mutableListOf(),
    val meta: MutableMap<String, Any> = mutableMapOf()
)

fun Activity.upload() {
    val logger = KotlinLogging.logger {}
    val moshi = Moshi.Builder().build()
    val adapter = ActivityJsonAdapter(moshi)

    val (_, _, result) = POST.httpPost()
        .header("content-type" to "application/json")
        .body(adapter.toJson(this))
        .responseString()

    result.success { logger.info(result.get()) }
    result.failure { logger.error("error sending data to database", result.component2()) }
}
