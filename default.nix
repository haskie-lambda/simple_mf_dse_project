{ java11?false, java14?false }:
with import<nixpkgs>{};
let baseInputs = with pkgs; [
        maven
        lsof
        nodejs
        yarn
        netcat-gnu
    ];
    buildInp = if (java11) then baseInputs ++ [pkgs.jdk11] 
                else if(java14) then baseInputs ++ [pkgs.jdk14]
                else baseInputs ++ [pkgs.openjdk];
in
pkgs.stdenv.mkDerivation {
    name="mf-debugging";
    version="1";
    buildInputs = buildInp;
    shellHook = ''
         getEchoRequest() { 
            echo "{ \"messageID\": \"`getUUID`\"; \"topic\": \"org.Team107.MF.Echo\"; \"data\": { \"uuid\": \"`getUUID`\"; \"id\": 1; \"otherInfo\": \"SD1\"; \"request\": \"true\" }}\""; 
            };
         alias getUUID="cat /proc/sys/kernel/random/uuid";
         sendEchoTo() {
            req=`getEchoRequest`
            printf "sending %s\n" $req
            echo "to $1"
            echo "press ^C to send the request"
            (echo "$req") | nc localhost $1
         }
        '';
}
