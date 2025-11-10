{
  description = "Nix flake for development of minecraft mods";
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    systems.url = "github:nix-systems/default";
    flake-utils = {
      url = "github:numtide/flake-utils";
      inputs.systems.follows = "systems";
    };
    nix-jebrains-plugins = {
      url = "github:theCapypara/nix-jebrains-plugins";
      inputs = {
        nixpkgs.follows = "nixpkgs";
	systems.follows = "systems";
	flake-utils.follows = "flake-utils";
      };
    };
  };
  outputs = { self, nixpkgs, flake-utils, nix-jebrains-plugins, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ self.overlays.${system}.default ];
        };
        lib = pkgs.lib;

	jdk = pkgs.jetbrains.jdk-no-jcef;

        ideaPlugins = [
          "com.github.catppuccin.jetbrains"
          "com.github.catppuccin.jetbrains_icons"
	  "com.intellij.plugins.vscodekeymap"
	  "com.demonwav.minecraft-dev"
	  "me.shedaniel.architectury"
	  "IdeaVIM"
	  "org.toml.lang"
        ];
      in {
        overlays.default = (final: prev:
          {
            maven = prev.maven.override { jdk_headless = jdk; };
            gradle = prev.gradle.override { java = jdk; };
            kotlin = prev.kotlin.override { jre = jdk; };
	    my_jdk = jdk;
          });

        packages.jetbrainsIde =
          (pkgs.jetbrains.plugins.addPlugins pkgs.jetbrains.idea-community-bin
            (map (plugin:
              nix-jebrains-plugins.plugins.${system}.idea-community."2025.2".${plugin})
              (ideaPlugins)));

        devShells = {
          default = pkgs.mkShell rec {
            nativeBuildInputs = with pkgs; [
              gcc
              gradle
              my_jdk
              maven
              kotlin
              ncurses
              patchelf
              zlib
            ];

            buildInputs = with pkgs; [
              glfw
              libpulseaudio
              libGL
              openal
              stdenv.cc.cc.lib
              flite # narrator

              vulkan-loader # VulkanMod's lwjgl

              udev # oshi

              xorg.libX11
              xorg.libXext
              xorg.libXcursor
              xorg.libXrandr
              xorg.libXxf86vm

              mesa-demos
              pciutils # need lspci
              xorg.xrandr # needed for LWJGL [2.9.2, 3) https://github.com/LWJGL/lwjgl/issues/128
            ];

            LD_LIBRARY_PATH =
              lib.makeLibraryPath (buildInputs ++ nativeBuildInputs);
          };
        };
      });
}
