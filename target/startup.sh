while true; do
    java -jar -Xms1024M -Xmx2048M -Dfile.encoding=UTF8 -jar BungeeCord.jar
    echo "Server is currently restarting"
    for i in 5 4 3 2 1; do
        echo "...$i"
        sleep 1
    done
done
