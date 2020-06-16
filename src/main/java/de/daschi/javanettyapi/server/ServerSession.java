package de.daschi.javanettyapi.server;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientRegistered;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientUnregistered;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerSession extends SimpleChannelInboundHandler<Packet> {

    private static final Map<UUID, Channel> channels = new HashMap<>();

    public static Map<UUID, Channel> getChannels() {
        return ServerSession.channels;
    }

    @Override
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
    }

    @Override
    public void channelInactive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelInactive(channelHandlerContext);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) {
        if (packet instanceof PacketPlayOutClientRegistered) {
            System.out.println("Client '" + ((PacketPlayOutClientRegistered) packet).getUuid() + "' connected from '" + channelHandlerContext.channel().remoteAddress() + "'.");
            ServerSession.channels.put(((PacketPlayOutClientRegistered) packet).getUuid(), channelHandlerContext.channel());
        } else if (packet instanceof PacketPlayOutClientUnregistered) {
            System.out.println("Client '" + ((PacketPlayOutClientUnregistered) packet).getUuid() + "' disconnected from '" + channelHandlerContext.channel().remoteAddress() + "'.");
            ServerSession.channels.remove(((PacketPlayOutClientUnregistered) packet).getUuid());
            channelHandlerContext.channel().close();

            if (ServerSession.channels.isEmpty() && Server.getServer().isShuttingDown()) {
                Server.getServer().getChannel().close();
            }
        }
    }
}
