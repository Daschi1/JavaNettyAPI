package de.daschi.javanettyapi.packets;

import de.daschi.javanettyapi.client.Client;
import de.daschi.javanettyapi.client.ClientSession;
import de.daschi.javanettyapi.core.Core;
import de.daschi.javanettyapi.server.ServerSession;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(final ChannelHandlerContext channelHandlerContext, final Packet packet, final ByteBuf byteBuf) {
        final int packedId = Core.getPacketId(packet.getClass());
        byteBuf.writeInt(packedId);
        if (ClientSession.getChannel() != null) {
            packet.writeUuid(byteBuf, Client.getClient().getUuid());
        }
        packet.write(byteBuf);
        if (ClientSession.getChannel() != null) {
            // System.out.println("Sent packet '" + packet.getClass().getSimpleName() + "'.");
            packet.clientSent();
        } else {
            ServerSession.getChannels().forEach((uuid, channel) -> {
                if (channel.id().asShortText().equals(channelHandlerContext.channel().id().asShortText())) {
                    // System.out.println("Sent packet '" + packet.getClass().getSimpleName() + "' to '" + uuid + "'.");
                    packet.serverSent(uuid);
                }
            });
        }
    }
}
