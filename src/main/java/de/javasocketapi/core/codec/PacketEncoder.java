package de.javasocketapi.core.codec;

import de.javasocketapi.core.packet.Packet;
import de.javasocketapi.core.packet.PacketIDHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final Packet packet, final ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(PacketIDHandler.getIdFromPacketClass(packet.getClass()));
        packet.send(byteBuf);
    }
}
