with import <nixpkgs> {};
stdenv.mkDerivation {
  name = "nix-cage-shell";
  buildInputs = [
    nodejs-8_x
    boot
    electron
    clojure
  ];
}
