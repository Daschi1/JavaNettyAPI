package de.daschi.javanettyapi.packets;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.UUID;

public abstract class Packet {

    public Packet() {
    }

    public abstract void write(ByteBuf byteBuf);

    public abstract void read(ByteBuf byteBuf);

    public abstract void clientSent();

    public abstract void clientReceived();

    public abstract void serverSent(final UUID uuid);

    public abstract void serverReceived(final UUID uuid);

    public void writeString(final ByteBuf byteBuf, final String s) {
        byteBuf.writeInt(s.length());
        byteBuf.writeCharSequence(s, Charset.defaultCharset());
    }

    public String readString(final ByteBuf byteBuf) {
        final int length = byteBuf.readInt();
        return byteBuf.readCharSequence(length, Charset.defaultCharset()).toString();
    }

    public void writeUuid(final ByteBuf byteBuf, final UUID uuid) {
        this.writeString(byteBuf, uuid.toString());
    }

    public UUID readUuid(final ByteBuf byteBuf) {
        return UUID.fromString(this.readString(byteBuf));
    }

}
