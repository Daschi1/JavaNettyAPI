package de.daschi.javanettyapi.client;

import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.server.PacketPlayOutClientDisconnect;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

public class ClientSession extends SimpleChannelInboundHandler<Packet> {

    private static Channel channel;

    public static Channel getChannel() {
        return ClientSession.channel;
    }

    @Override
    public void channelActive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);

        // System.out.println("Connected to '" + channelHandlerContext.channel().remoteAddress() + "'.");
        ClientSession.channel = channelHandlerContext.channel();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelInactive(channelHandlerContext);

        // System.out.println("Disconnected to '" + channelHandlerContext.channel().remoteAddress() + "'.");
        ClientSession.channel = null;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final Packet packet) {
        if (packet instanceof PacketPlayOutClientDisconnect) {
            CompletableFuture.runAsync(() -> Client.getClient().disconnect());
        }
    }
}
