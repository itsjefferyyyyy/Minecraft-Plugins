package de.jefferySHong.untitled

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class CoordinateStorage(private val plugin: JavaPlugin) {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File(plugin.dataFolder, "coordinates.json")
    private val coordinates = LinkedHashMap<String, Coordinate>()

    fun load() {
        coordinates.clear()
        if (!file.exists()) return

        try {
            val type = object : TypeToken<List<Coordinate>>() {}.type
            val list: List<Coordinate>? = file.reader(Charsets.UTF_8).use { gson.fromJson(it, type) }
            list?.forEach { coordinates[it.name.lowercase()] = it }
            plugin.logger.info("${coordinates.size} Koordinaten geladen.")
        } catch (e: Exception) {
            plugin.logger.warning("Konnte ${file.name} nicht laden: ${e.message}")
        }
    }

    fun save() {
        try {
            file.parentFile.mkdirs()
            file.writer(Charsets.UTF_8).use { gson.toJson(coordinates.values.toList(), it) }
        } catch (e: Exception) {
            plugin.logger.warning("Konnte ${file.name} nicht speichern: ${e.message}")
        }
    }

    /** Speichert den Punkt. Gibt true zurück, wenn ein vorhandener Punkt überschrieben wurde. */
    fun put(coordinate: Coordinate): Boolean {
        val replaced = coordinates.put(coordinate.name.lowercase(), coordinate) != null
        save()
        return replaced
    }

    fun remove(name: String): Boolean {
        val removed = coordinates.remove(name.lowercase()) != null
        if (removed) save()
        return removed
    }

    fun all(): Collection<Coordinate> = coordinates.values

    fun names(): List<String> = coordinates.values.map { it.name }
}
