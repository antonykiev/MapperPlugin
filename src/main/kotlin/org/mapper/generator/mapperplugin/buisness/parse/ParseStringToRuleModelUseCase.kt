package org.mapper.generator.mapperplugin.buisness.parse

import com.google.gson.Gson
import org.mapper.generator.mapperplugin.data.states.SimpleJsonRulesModel

class ParseStringToRuleModelUseCase {

    operator fun invoke(jsonString: String): Result<SimpleJsonRulesModel> {
        return runCatching {
            Gson().fromJson(jsonString, SimpleJsonRulesModel::class.java)
        }
    }
}