package br.dev.henriquekh.coords_finder.fabric;

import net.fabricmc.api.ModInitializer;

import br.dev.henriquekh.coords_finder.CoordsFinder;

public final class CoordsFinderFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CoordsFinder.init();
    }
}
