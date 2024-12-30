package br.dev.henriquekh.coords_finder

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object PlayerSuggestionProvider : SuggestionProvider<ServerCommandSource> {
  override fun getSuggestions(
    context: CommandContext<ServerCommandSource?>?,
    builder: SuggestionsBuilder?,
  ): CompletableFuture<Suggestions?>? {
    context?.source?.playerNames?.forEach {
      builder?.suggest(it)
    }
    return builder?.buildFuture()
  }
}