package de.daschi.javanettyapi.packets.server;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketID;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

@PacketID(-3)
public class PacketPlayOutClientDisconnect extends Packet {

    public PacketPlayOutClientDisconnect() {
    }

    @Override
    public void write(final ByteBuf byteBuf) {

    }

    @Override
    public void read(final ByteBuf byteBuf) {

    }

    @Override
    public void clientSent() {

    }

    @Override
    public void clientReceived() {

    }

    @Override
    public void serverSent(UUID uuid) {

    }

    @Override
    public void serverReceived(UUID uuid) {

    }
}
