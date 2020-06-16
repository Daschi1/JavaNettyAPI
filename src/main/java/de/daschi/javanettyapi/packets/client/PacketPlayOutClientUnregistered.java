package de.daschi.javanettyapi.packets.client;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketID;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

@PacketID(-2)
public class PacketPlayOutClientUnregistered extends Packet {

    private UUID uuid;

    public PacketPlayOutClientUnregistered() {
    }

    public PacketPlayOutClientUnregistered(final UUID uuid) {
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
    public void serverSent(final UUID uuid) {

    }

    @Override
    public void serverReceived(final UUID uuid) {

    }

    public UUID getUuid() {
        return this.uuid;
    }
}