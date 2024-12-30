package br.dev.henriquekh.coords_finder

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory

object CoordsFinder : ModInitializer {
  private val logger = LoggerFactory.getLogger("coords-finder")
  private var disabled = false

  override fun onInitialize() {
    logger.info("Initializing CoordsFinder")

    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
      logger.info("Initializing CoordsFinder Commands")
      dispatcher.register(
        CommandManager.literal("coords").executes(::coords).then(
          CommandManager.argument("player", StringArgumentType.string())
            .suggests(
              PlayerSuggestionProvider
            ).executes {
              coords(
                it, player = it.source.server.playerManager.getPlayer(
                  StringArgumentType.getString(it, "player")
                )
              )
            })
      )
      dispatcher.register(
        CommandManager.literal("coords-toggle")
          .requires { it.hasPermissionLevel(2) }
          .executes {
            disabled = !disabled
            1
          })
    }
  }

  fun coords(
    command: CommandContext<ServerCommandSource>,
    player: ServerPlayerEntity? = command.source.player,
  ): Int {
    if (disabled) {
      command.source.sendMessage(Text.translatable("coords-finder.disabled"))
      return 0
    }

    if (player == null) {
      command.source.sendMessage(Text.translatable("coords-finder.error"))
      return 0
    }

    val (x, y, z) = player.blockPos

    command.source.server.playerManager.playerList.forEach { player ->
      player.sendMessage(
        Text.translatable(
          "coords-finder.success",
          player.displayName,
          Text.literal("XYZ: ").append(x.toString()).append(" ")
            .append(y.toString()).append(" ")
            .append(z.toString()).setStyle(
              Style.EMPTY.withUnderline(true).withColor(
                TextColor.fromRgb(0x00FFFF)
              ).withClickEvent(
                ClickEvent(
                  ClickEvent.Action.COPY_TO_CLIPBOARD,
                  "$x $y $z"
                )
              )
            ),
          player.world.getBiome(player.blockPos).key.get().value.toString()
        )
      )
    }

    return 1
  }
}

operator fun BlockPos.component1() = this.x
operator fun BlockPos.component2() = this.y
operator fun BlockPos.component3() = this.z