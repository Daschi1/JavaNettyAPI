# JavaNettyAPI

## A java [netty](https://netty.io/) api with [reflections](https://github.com/ronmamo/reflections).

# Usage

Server:

```java
Server server = new Server(19503); //create server
server.connect(); //connect server
server.disconnect(); //disconnect server

server.sendPacket(/*client-uuid*/, /*packet*/); //send a packet to a specific client
server.sendPacketToAll(/*packet*/); //send a packet to all clients

server.disconnectClient(/*client-uuid*/); //disconnect a specific client
server.disconnectAllClients(); //disconnect all clients
```

Client:

```java
Client client = new Client("localhost", 19503); //create client
client.connect(); //connect client
client.disconnect(); //disconnect client

client.getUuid(); //get client-uuid

client.sendPacket(/*packet*/); //send a packet to the server
```

Packet:

```java

Core.loadPackets(/*package to load packets from*/); //register all of your packets in a package

@PacketID(0) //packetId, must be unique and zero or above for each packet to match the equivalent client- and serverside packet
public class TestPacket extends Packet { //extends from packet

    private String testMessage; //any variables

    public TestPacket() { //empty constructor needs to be present
    }

    public TestPacket(final String testMessage) { //any other constructors
        this.testMessage = testMessage;
    }

    @Override
    public void write(final ByteBuf byteBuf) { //write variables
        byteBuf.writeInt(0);
        this.writeString(byteBuf, this.testMessage);
    }

    @Override
    public void read(final ByteBuf byteBuf) { //and read them ins the same order
        int i = byteBuf.readInt();
        this.testMessage = this.readString(byteBuf);
    }

    @Override
    public void clientSent() { //called when the client sent the packet

    }

    @Override
    public void clientReceived() { //called when the client recieved the packet
        
    }

    @Override
    public void serverSent(final UUID uuid) { //called when the server sent the packet to the client-uuid

    }

    @Override
    public void serverReceived(final UUID uuid) { //called when the server recieved the packet from the client-uuid

    }
}
```
