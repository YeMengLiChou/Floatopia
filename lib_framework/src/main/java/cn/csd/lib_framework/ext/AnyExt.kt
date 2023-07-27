package cn.csd.lib_framework.ext

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */


@Suppress("UNCHECKED_CAST")
fun <T> Any.uncheckAs(): T {
    return this as T
}