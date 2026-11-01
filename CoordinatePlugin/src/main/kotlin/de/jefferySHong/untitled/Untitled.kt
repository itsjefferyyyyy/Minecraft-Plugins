package de.jefferySHong.untitled

import org.bukkit.plugin.java.JavaPlugin

class Untitled : JavaPlugin() {

    lateinit var storage: CoordinateStorage

    override fun onEnable() {
        storage = CoordinateStorage(this).also { it.load() }
        registerCommand(
            "coordinate",
            "Koordinaten speichern und anzeigen",
            listOf("coord"),
            CoordinateCommand(storage)
        )
    }

    override fun onDisable() {
        storage.save()
    }
}
