package br.dev.henriquekh.coords_finder;


import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoordsFinder {
  public static final String MOD_ID = "coords_finder";

  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static void init() {
    LOGGER.info("Initializing CoordsFinder");

    var commands = new Commands();

    CommandRegistrationEvent.EVENT.register((dispatcher, registerAccess, environment) -> {
      LOGGER.info("Initializing CoordsFinder Commands");
      dispatcher.register(commands.getCoords());
      dispatcher.register(commands.getCoordsToggle());
    });
  }
}
