# Paper Rock Scissors Multiplayer
This java project allows you to play the iconic game paper-rock-scissors now in **multiplayer** mode!

The game automatically counts your points, and you can decide for good which is better.

## How to run
To run Server you need to compile program using `javac` command (download `java-jdk` if you aren't instlled yet)
```
javac ./Client.java ./Server/Server.java
```
Then run the server using:
```
cd ./Server
java Server <port> <players> <rounds (optional)>
```
- set the `port` so that it doesn't conflict with other applications
- Set `players` - the maximum number of players on server
- Set `rounds` (optional) - if you want to limit the number of rounds (the server will be shut down if the limit is reached)

Example: `java Server 12345 4 5`

### Client
Other players run a `Client` program to play
```
java Client <host> <port>
```
- `host` - host server: `localhost` or server ip
- `port` - must be same to the server's port

Enjoy ;)
