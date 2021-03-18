package com.foobarust.deliverer.api.deserializers

import com.foobarust.deliverer.data.response.DirectionsResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * Created by kevin on 3/8/21
 */

class DirectionsDeserializer : JsonDeserializer<DirectionsResponse> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DirectionsResponse {
        val obj = json?.asJsonObject ?: throw JsonParseException("Cannot parse directions")
        // Use first route only
        val route = obj.getAsJsonArray("routes")[0].asJsonObject
        val polyline = route.get("overview_polyline").asJsonObject
        val points = polyline.get("points").asString

        return DirectionsResponse(points)
    }
}