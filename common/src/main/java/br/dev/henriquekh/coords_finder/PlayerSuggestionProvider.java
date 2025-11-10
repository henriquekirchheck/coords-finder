package br.dev.henriquekh.coords_finder;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class PlayerSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
  @Override
  public CompletableFuture<Suggestions> getSuggestions(
    CommandContext<CommandSourceStack> context,
    SuggestionsBuilder builder
  ) {
    for (var player : context.getSource().getOnlinePlayerNames()) {
      builder.suggest(player);
    }
    return builder.buildFuture();
  }
}
