package com.example.currentweather.models

import com.google.gson.JsonObject

interface BaseDeserializer {

    fun JsonObject.getJsonArrayElement(memberName: String): JsonObject? {
        return try {
            this.get(memberName).asJsonArray[0].asJsonObject
        } catch (th: Throwable) {
            null
        }
    }

    fun JsonObject.getJsonObject(memberName: String): JsonObject? {
        return try {
            this.get(memberName).asJsonObject
        } catch (th: Throwable) {
            null
        }
    }

    fun JsonObject.getDouble(memberName: String): Double? {
        return try {
            this.get(memberName).asDouble
        } catch (th: Throwable) {
            null
        }
    }

    fun JsonObject.getInt(memberName: String): Int? {
        return try {
            this.get(memberName).asInt
        } catch (th: Throwable) {
            null
        }
    }

    fun JsonObject.getLong(memberName: String): Long? {
        return try {
            this.get(memberName).asLong
        } catch (th: Throwable) {
            null
        }
    }

    fun JsonObject.getString(memberName: String): String? {
        return try {
            this.get(memberName).asString
        } catch (th: Throwable) {
            null
        }
    }
}