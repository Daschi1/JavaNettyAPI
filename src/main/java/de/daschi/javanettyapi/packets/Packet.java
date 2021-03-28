package de.daschi.javanettyapi.packets;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.UUID;
import java.util.stream.IntStream;

public abstract class Packet {

    public Packet() {
    }

    public abstract void write(ByteBuf byteBuf);

    public abstract void read(ByteBuf byteBuf);

    public abstract void clientSent();

    public abstract void clientReceived();

    public abstract void serverSent(final UUID uuid);

    public abstract void serverReceived(final UUID uuid);

    public void writeString(final ByteBuf byteBuf, final String s, final Charset charset) {
        final byte[] bytes = s.getBytes(charset);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public String readString(final ByteBuf byteBuf, final Charset charset) {
        final int length = byteBuf.readInt();
        final byte[] bytes = new byte[length];
        IntStream.range(0, length).forEach(i -> bytes[i] = byteBuf.readByte());
        return new String(bytes, charset);
    }

    public void writeUuid(final ByteBuf byteBuf, final UUID uuid) {
        this.writeString(byteBuf, uuid.toString(), Charset.defaultCharset());
    }

    public UUID readUuid(final ByteBuf byteBuf) {
        return UUID.fromString(this.readString(byteBuf, Charset.defaultCharset()));
    }

}
