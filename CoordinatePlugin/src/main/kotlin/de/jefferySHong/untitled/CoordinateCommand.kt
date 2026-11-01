package de.jefferySHong.untitled

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import kotlin.math.floor

class CoordinateCommand(private val storage: CoordinateStorage) : BasicCommand {

    private val subcommands = listOf("save", "list", "delete")

    override fun execute(source: CommandSourceStack, args: Array<String>) {
        val sender = source.sender
        when (args.firstOrNull()?.lowercase()) {
            "save" -> {
                val player = source.executor as? Player
                if (player == null) {
                    sender.sendMessage(Component.text("Nur Spieler können Koordinaten speichern.", NamedTextColor.RED))
                    return
                }
                val name = args.getOrNull(1)
                if (name == null) {
                    sender.sendMessage(Component.text("Benutzung: /coordinate save <name>", NamedTextColor.RED))
                    return
                }
                val loc = player.location
                val replaced = storage.put(Coordinate(name, loc.world.name, loc.x, loc.y, loc.z, player.name))
                val text = if (replaced) "Koordinate '$name' überschrieben: " else "Koordinate '$name' gespeichert: "
                sender.sendMessage(Component.text(text + format(loc.world.name, loc.x, loc.y, loc.z), NamedTextColor.GREEN))
            }

            "list" -> {
                val all = storage.all()
                if (all.isEmpty()) {
                    sender.sendMessage(Component.text("Keine Koordinaten gespeichert.", NamedTextColor.YELLOW))
                    return
                }
                sender.sendMessage(Component.text("Gespeicherte Koordinaten (${all.size}):", NamedTextColor.GOLD))
                all.forEach {
                    sender.sendMessage(
                        Component.text("- ", NamedTextColor.GRAY)
                            .append(Component.text(it.name, NamedTextColor.AQUA))
                            .append(Component.text(" – ${format(it.world, it.x, it.y, it.z)}", NamedTextColor.WHITE))
                            .append(Component.text(" – von ${it.savedBy}", NamedTextColor.GRAY))
                    )
                }
            }

            "delete" -> {
                val name = args.getOrNull(1)
                if (name == null) {
                    sender.sendMessage(Component.text("Benutzung: /coordinate delete <name>", NamedTextColor.RED))
                    return
                }
                if (storage.remove(name)) {
                    sender.sendMessage(Component.text("Koordinate '$name' gelöscht.", NamedTextColor.GREEN))
                } else {
                    sender.sendMessage(Component.text("Koordinate '$name' nicht gefunden.", NamedTextColor.RED))
                }
            }

            else -> sender.sendMessage(
                Component.text("Benutzung: /coordinate <save <name> | list | delete <name>>", NamedTextColor.RED)
            )
        }
    }

    override fun suggest(source: CommandSourceStack, args: Array<String>): Collection<String> = when {
        args.size <= 1 -> subcommands.filter { it.startsWith(args.firstOrNull()?.lowercase() ?: "") }
        args.size == 2 && args[0].equals("delete", ignoreCase = true) ->
            storage.names().filter { it.startsWith(args[1], ignoreCase = true) }
        else -> emptyList()
    }

    private fun format(world: String, x: Double, y: Double, z: Double): String =
        "$world (${floor(x).toInt()}, ${floor(y).toInt()}, ${floor(z).toInt()})"
}
