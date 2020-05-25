package de.javasocketapi.core;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Packet {

    void read(ByteBuf byteBuf) throws IOException;

    void write(ByteBuf byteBuf) throws IOException;
}
