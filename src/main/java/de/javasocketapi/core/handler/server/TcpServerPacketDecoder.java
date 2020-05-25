package de.javasocketapi.core.handler.server;

import de.javasocketapi.core.Packet;
import de.javasocketapi.core.tcp.server.TcpServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TcpServerPacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> output) throws Exception {
        int id = byteBuf.readInt();
        Class<? extends Packet> packetClass = TcpServer.PACKETS_PLAY_IN.get(id); // TODO: 25.05.2020 make via id
        if (packetClass == null) {
            throw new IllegalArgumentException("PacketId '" + id + "' is not registerd.");
        }

        Packet packet = packetClass.newInstance();
        packet.read(byteBuf);

        output.add(packet);
    }
}
