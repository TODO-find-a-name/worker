package app

import io.github.cdimascio.dotenv.dotenv
import java.util.*

class StringEnvVariableReader {
    companion object {
        fun readOptionalString(varName: String): Optional<String> {
            var res = try {
                dotenv()[varName]
            } catch (e: Exception){
                try {
                    System.getenv(varName)
                } catch (e: Exception){
                    return Optional.empty()
                }
            }

            if(res == null){
                return Optional.empty()
            }

            res = res.trim()
            if(res.isEmpty()){
                return Optional.empty()
            }
            return Optional.of(res)
        }
    }
}