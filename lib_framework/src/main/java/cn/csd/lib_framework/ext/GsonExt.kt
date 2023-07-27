package cn.csd.lib_framework.ext

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Gson 扩展类
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/19
 */

/**
 * 将所给参数 [args] 生成 [JsonObject] 实例
 * @param args
 * @return JsonObject
 * */
fun jsonObjectOf(vararg args: Pair<String, Any?>) = JsonObject().append(*args)


fun JsonObject.append(other: JsonObject?): JsonObject {
    other?.entrySet()?.forEach { (name, value) ->
        append(name, value)
    }
    return this
}
/**
 * 重载运算符 `+`
 * */
operator fun JsonObject?.plus(other: JsonObject?): JsonObject = jsonObjectOf().also {
    it.append(this).append(other)
}

/**
 * 重载运算符 `+=`
 * */
operator fun JsonObject.plusAssign(other: JsonObject?) {
    this.append(other)
}

fun JsonObject.append(name: String, value: String?): JsonObject {
    value?.let { addProperty(name, it) }
    return this
}

fun JsonObject.append(name: String, value: Number?): JsonObject {
    value?.let { addProperty(name, it) }
    return this
}

fun JsonObject.append(name: String, value: Boolean?): JsonObject {
    value?.let { addProperty(name, it) }
    return this
}

fun JsonObject.append(name: String, value: Char?): JsonObject {
    value?.let { addProperty(name, it) }
    return this
}

fun JsonObject.append(name: String, value: JsonElement?): JsonObject {
    value?.let { add(name, it) }
    return this
}


fun JsonObject.append(vararg args: Pair<String, Any?>): JsonObject {
    args.forEach { (name, value) ->
        value?.let {
            when (it) {
                is String -> append(name, it)
                is Number -> append(name, it)
                is Boolean -> append(name, it)
                is Char -> append(name, it)
                is JsonElement -> append(name, it)
                else -> append(name, it.toString())
            }
        }
    }
    return this
}

/**
 * 如果 string 是 json，则转为 [JsonElement]
 * @return 不是 json 返回 null
 * */
fun String.toJsonElement(): JsonElement? {
    if (!isJSONValid()) return null
    return JsonParser.parseString(this)
}

/**
 * 如果 string 是 json，则转为 [JsonObject]
 * @return 不是 json 返回 null
 * */
fun String.toJsonObject(): JsonObject? {
    return toJsonElement()?.asJsonObject
}

/**
 * 如果 string 是 json 则返回规范的 json
 * @return prettyJson
 * */
val String.prettyJson: String
    get() {
        if (!isJSONValid()) return ""
        return JsonParser.parseString(this).toPrettyJson()
    }

inline fun <reified T> JsonElement.asBean(): T? {
    return if (isJsonObject) toBean() else null
}

fun String.isJSONValid(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        // edited, to include @Arthur's comment
        // e.g. in case JSONArray is valid as well...
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            return false
        }
    }
    return true
}

inline fun <reified T> JsonElement.toBeanOrNull(includeNulls: Boolean = true): T? =
    runCatching { toBean<T>() }.getOrNull()

val JsonElement.asJsonObjectOrNull: JsonObject?
    get() = runCatching { asJsonObject }.getOrNull()

val JsonElement.asJsonArrayOrNull: JsonArray?
    get() = runCatching { asJsonArray }.getOrNull()

val JsonElement.asStringOrNull: String?
    get() = runCatching { asString }.getOrNull()

val JsonElement.asStringOrEmpty: String
    get() = runCatching { asString }.getOrNull() ?: ""

val JsonElement.asIntOrNull: Int?
    get() = runCatching { asInt }.getOrNull()

val JsonElement.asBooleanOrNull: Boolean?
    get() = runCatching { asBoolean }.getOrNull()

/**
 * 将Bean对象转成[JsonObject]
 */
inline fun <reified T> T.toJsonObject(includeNulls: Boolean = true): JsonObject {
    return gson(includeNulls).toJsonTree(this, object : TypeToken<T>() {}.type).asJsonObject
}

private val GSON by lazy {
    GsonBuilder().create()
}


private val GSON_NO_NULLS by lazy {
    GsonBuilder().serializeNulls().create()
}

private val GSON_PRETTY by lazy {
    GsonBuilder().setPrettyPrinting().create()
}

/**
 * 有些json窜有引号，去除
 */
fun String.parseJson() = JsonParser.parseString(this)

/**
 * 将 Bean 对象转换成 json 字符串
 *
 * @receiver 任意Bean对象
 * @param includeNulls 是否包含值为空的字段（默认包含）
 * @return 转换后的json字符串
 */
fun Any.toJson(includeNulls: Boolean = true): String {
    return gson(includeNulls).toJson(this)
}

/**
 * 将Bean对象转换成可读性高的json字符串
 *
 * @receiver 任意Bean对象
 * @return 转换后的json字符串
 */
fun Any.toPrettyJson(): String {
    return GSON_PRETTY.toJson(this)
}

/**
 * 将json字符串转换成目标Bean对象
 *
 * @receiver json字符串
 * @param includeNulls 是否包含值为空的字段（默认包含）
 * @return 转换后的Bean对象
 */
inline fun <reified T> String.toBean(includeNulls: Boolean = true): T {
    return gson(includeNulls).fromJson(this, object : TypeToken<T>() {}.type)
}

inline fun <reified T> String.toBeanOrNull(includeNulls: Boolean = true): T? =
    runCatching { toBean<T>() }.onFailure { it.printStackTrace() }.getOrNull()

/**
 * 将 json 字符串转换成目标 Bean 对象
 *
 * @receiver json元素
 * @param includeNulls 是否包含值为空的字段（默认包含）
 * @return 转换后的Bean对象
 */
inline fun <reified T> JsonElement.toBean(includeNulls: Boolean = true): T {
    return gson(includeNulls).fromJson(this, object : TypeToken<T>() {}.type)
}

fun gson(includeNulls: Boolean): Gson = if (includeNulls) GSON else GSON_NO_NULLS
