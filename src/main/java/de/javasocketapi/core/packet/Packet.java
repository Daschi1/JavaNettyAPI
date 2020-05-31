package de.javasocketapi.core.packet;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public abstract class Packet {

    public Packet() {
    }

    public abstract void send(ByteBuf byteBuf) throws IOException;

    public abstract void receive(ByteBuf byteBuf) throws IOException;
}
