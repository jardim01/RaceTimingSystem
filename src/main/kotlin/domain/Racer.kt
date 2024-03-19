package domain

import kotlinx.serialization.Serializable

@Serializable
data class Racer(val id: Int, val name: String, val times: RaceTimes? = null)
