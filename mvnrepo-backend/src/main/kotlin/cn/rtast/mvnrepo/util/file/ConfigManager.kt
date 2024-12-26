/*
 * Copyright © 2024 RTAkland
 * Author: RTAkland
 * Date: 2024/12/26
 */


package cn.rtast.mvnrepo.util.file

import cn.rtast.mvnrepo.DEFAULT_CONFIG
import cn.rtast.mvnrepo.STORAGE_PATH
import cn.rtast.mvnrepo.entity.config.Config
import cn.rtast.mvnrepo.util.str.fromJson
import cn.rtast.mvnrepo.util.str.toJson
import java.io.File

class ConfigManager {

    private val file = File(STORAGE_PATH, "config.json").apply {
        if (!exists()) {
            createNewFile()
            writeText(DEFAULT_CONFIG.toJson())
        }
    }

    fun readConfig(): Config {
        return file.readText().fromJson<Config>()
    }

    fun writeConfig(config: Config) {
        file.writeText(config.toJson())
    }
}