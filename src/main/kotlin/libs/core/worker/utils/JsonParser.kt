package libs.core.worker.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import java.util.*

class JsonParser {

    private val gson: Gson = GsonBuilder().create()

    fun <T>fromJson(msg: String, of: Class<T>): Optional<T & Any> {
        return try {
            val parsed: T = gson.fromJson(msg, of)
            if(parsed == null){
                return Optional.empty()
            } else {
                return Optional.of(parsed)
            }
        } catch (e: JsonParseException){
            Optional.empty()
        }
    }

    fun toJson(element: Any): String {
        return gson.toJson(element)
    }

}