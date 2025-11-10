package br.dev.henriquekh.coords_finder;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

public class Commands {
  private boolean disabled = false;

  private final LiteralArgumentBuilder<CommandSourceStack> coords =
      net.minecraft.commands.Commands.literal("coords").executes(this::coords).then(
          net.minecraft.commands.Commands.argument("player", StringArgumentType.string())
              .suggests(new PlayerSuggestionProvider()).executes(command -> {
                return coords(
                    command, command.getSource().getServer().getPlayerList()
                        .getPlayer(StringArgumentType.getString(command, "player"))
                );
              }));

  private final LiteralArgumentBuilder<CommandSourceStack> coords_toggle =
      net.minecraft.commands.Commands.literal("coords-toggle").requires(sourceStack -> {
        return sourceStack.hasPermission(2);
      }).executes(command -> {
        disabled = !disabled;
        return 1;
      });

  private int coords(CommandContext<CommandSourceStack> command) {
    return coords(command, command.getSource().getPlayer());
  }

  private int coords(CommandContext<CommandSourceStack> command, ServerPlayer player) {
    if (disabledCheck(command))
      return 0;
    if (player == null) {
      command.getSource().sendSystemMessage(
          Component.translatableWithFallback(
              "coords-finder.error",
              "Specified Player was not found"
          ));
      return 0;
    }

    var coords = "%d %d %d".formatted(
        player.blockPosition().getX(), player.blockPosition().getY(),
        player.blockPosition().getZ()
    );

    var biome = player.level().getBiome(player.blockPosition()).getRegisteredName();
    var msg = Component.translatableWithFallback(
        "coords-finder.success", "Player %1$s is located at %2$s on %3$s", player.getDisplayName(),
        Component.literal("XYZ: ").append(coords).setStyle(Style.EMPTY.withUnderlined(true)
            .withColor(TextColor.fromRgb(0x00FFFF))
            .withClickEvent(new ClickEvent.CopyToClipboard(coords))), biome
    );

    for (var player_msg : command.getSource().getServer().getPlayerList().getPlayers()) {
      player_msg.sendSystemMessage(msg);
    }

    return 1;
  }

  private boolean disabledCheck(CommandContext<CommandSourceStack> command) {
    if (disabled) {
      command.getSource().sendSystemMessage(
          Component.translatableWithFallback(
              "coords-finder.disabled",
              "/coords was deactivated by a administrator"
          ));
      return true;
    }
    return false;
  }

  public LiteralArgumentBuilder<CommandSourceStack> getCoordsToggle() {
    return coords_toggle;
  }

  public LiteralArgumentBuilder<CommandSourceStack> getCoords() {
    return coords;
  }
}
