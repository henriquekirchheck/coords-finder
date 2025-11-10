package br.dev.henriquekh.coords_finder.neoforge;

import net.neoforged.fml.common.Mod;

import br.dev.henriquekh.coords_finder.CoordsFinder;

@Mod(CoordsFinder.MOD_ID)
public final class CoordsFinderNeoForge {
    public CoordsFinderNeoForge() {
        // Run our common setup.
        CoordsFinder.init();
    }
}
