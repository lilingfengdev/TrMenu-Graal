package io.lilingfeng.trmenu.graal

import java.util.concurrent.ConcurrentHashMap
import javax.script.Compilable
import javax.script.CompiledScript
import javax.script.ScriptEngineManager
import javax.script.ScriptContext


object JavaScriptAgent {


    private val compiledScripts = ConcurrentHashMap<String, CompiledScript>()

    private val compiler by lazy {
        val engine = ScriptEngineManager(this::class.java.classLoader).getEngineByName("js")
        engine.getBindings(ScriptContext.ENGINE_SCOPE)["polyglot.js.allowAllAccess"] = true
        engine as Compilable
    }

    fun preCompile(script: String): CompiledScript =
        compiledScripts.computeIfAbsent(script) {
            compiler.compile(script)
        }


    fun eval(context: ScriptContext, script: String, cacheScript: Boolean = true): Any? {
        synchronized(compiledScripts) {
            val compiledScript =
                if (cacheScript) preCompile(script)
                else compiler.compile(script)
            return compiledScript.eval(context)
        }
    }
}

