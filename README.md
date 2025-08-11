# Paper Rock Scissors Multiplayer
This java project allows you to play the iconic game paper-rock-scissors now in **multiplayer** mode!

The game automatically counts your points, and you can decide for good which is better.

## How to run
To run Server you must first compile project using `javac` (you must download `java-jdk` first)
```
javac ./Client.java ./Server/Server.java
```
Then run the server using:
```
cd ./Server
java Server <port> <players> <rounds (optional)>
```
- set the `port` so that it does not interfere with other applications
- Set `players` to the maximum number of players on a single server
- Set `rounds` (optional) if you want to limit the number of rounds (the server will be shut down if the limit is exceeded)

Example: `java Server 12345 4 (5)`

### Client
Orther players run a `Client` program to play
```
java Client <host> <port>
```
- `host` - to the local host `localhost`
- `port` - set it to execute on the Server port

Enjoy ;)