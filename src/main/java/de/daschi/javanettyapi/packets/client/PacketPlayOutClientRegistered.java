package de.daschi.javanettyapi.packets.client;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketID;
import de.daschi.javanettyapi.server.ServerSession;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

@PacketID(-1)
public class PacketPlayOutClientRegistered extends Packet {

    private UUID uuid;

    public PacketPlayOutClientRegistered() {
    }

    public PacketPlayOutClientRegistered(final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        this.writeUuid(byteBuf, this.uuid);
    }

    @Override
    public void read(final ByteBuf byteBuf) {
        this.uuid = this.readUuid(byteBuf);
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

    public UUID getUuid() {
        return this.uuid;
    }
}