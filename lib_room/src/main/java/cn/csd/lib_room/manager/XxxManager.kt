package cn.csd.lib_room.manager

/**
 * 示例：管理全局唯一示例 dao，然后使用 **代理模式** 代理该 dao
 * ```
 * object VideoManager {
 *      private val dao by lazy { database.getXxxDap() }
 *
 *      fun getXxx(): List<Xxx> {
 *          dao.getXxx() // 代理 dao 对象的方法
 *      }
 * }
 * ```
 * 到时候通过 VideoManager.getXxx() 方法获取数据
 * @author Gleamrise
 * <br/>Created: 2023/07/18
 */
object XxxManager {


}