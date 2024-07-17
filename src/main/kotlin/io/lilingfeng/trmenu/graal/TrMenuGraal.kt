package io.lilingfeng.trmenu.graal

import org.bukkit.plugin.java.JavaPlugin
import javax.script.ScriptEngineManager


class TrMenuGraal : JavaPlugin() {
    override fun onEnable() {
        // ensure config file exists
        saveDefaultConfig()

        logger.info("TrMenu GraalJS 语言扩展已启动")
        JavaScriptAgent.preCompile("1+1") // 预热
    }
}
