with import <nixpkgs> {};
stdenv.mkDerivation {
  name = "nix-cage-shell";
  buildInputs = [
    gnumake
    git
    nodejs-8_x
    boot
    electron
    clojure
  ];
}
