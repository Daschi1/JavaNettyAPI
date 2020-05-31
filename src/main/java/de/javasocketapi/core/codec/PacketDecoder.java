package de.javasocketapi.core.codec;

import de.javasocketapi.core.packet.Packet;
import de.javasocketapi.core.packet.PacketIDHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.lang.reflect.Constructor;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> output) throws Exception {
        final int id = byteBuf.readInt();
        final Constructor<? extends Packet> constructor = PacketIDHandler.getPacketClassFromId(id).getDeclaredConstructor();
        constructor.setAccessible(true);
        final Packet packet = constructor.newInstance();
        packet.receive(byteBuf);
        output.add(packet);
    }
}
