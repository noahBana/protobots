package org.strykeforce.motion

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Activity(
  val name: String,
  var profileTicks: Int = 0,
  var actualTicks: Int = 0,
  var actualDistance: Double = 0.0,
  val data: MutableList<List<Double>> = mutableListOf(),
  val meta: MutableMap<String, Any> = mutableMapOf()
)
