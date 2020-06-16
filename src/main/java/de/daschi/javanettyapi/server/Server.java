package de.daschi.javanettyapi.server;

import de.daschi.javanettyapi.core.Core;
import de.daschi.javanettyapi.core.JavaNettyAPIException;
import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketDecoder;
import de.daschi.javanettyapi.packets.PacketEncoder;
import de.daschi.javanettyapi.packets.server.PacketPlayOutClientDisconnect;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.UUID;

public class Server {

    private static Server server;

    public static Server getServer() {
        return Server.server;
    }

    private final int port;
    private boolean shuttingDown;
    private final EventLoopGroup eventLoopGroup;
    private Channel channel;

    public Server(final int port) {
        Server.server = this;
        this.port = port;
        this.shuttingDown = false;

        this.eventLoopGroup = Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public void connect() {
        try {
            this.channel = new ServerBootstrap().group(this.eventLoopGroup).channel(Core.EPOLL_IS_AVAILABLE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel channel) {
                    channel.pipeline().addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ServerSession());
                }
            }).bind(this.port).sync().channel();
        } catch (final InterruptedException exception) {
            throw new JavaNettyAPIException("Could not bind the server on '" + this.port + "'.", exception);
        }
    }

    public void disconnect() {
        this.shuttingDown = true;
        this.disconnectAllClients();

        this.channel.closeFuture().awaitUninterruptibly();
        this.eventLoopGroup.shutdownGracefully();
    }

    public void sendPacket(final UUID uuid, final Packet packet) {
        ServerSession.getChannels().get(uuid).writeAndFlush(packet, ServerSession.getChannels().get(uuid).voidPromise());
    }

    public void sendPacketToAll(final Packet packet) {
        ServerSession.getChannels().forEach((uuid, channel) -> this.sendPacket(uuid, packet));
    }

    public void disconnectClient(final UUID uuid) {
        ServerSession.getChannels().get(uuid).writeAndFlush(new PacketPlayOutClientDisconnect(), ServerSession.getChannels().get(uuid).voidPromise());
    }

    public void disconnectAllClients() {
        ServerSession.getChannels().forEach((uuid, channel) -> this.disconnectClient(uuid));
    }

    public int getPort() {
        return this.port;
    }

    public boolean isShuttingDown() {
        return this.shuttingDown;
    }

    public EventLoopGroup getEventLoopGroup() {
        return this.eventLoopGroup;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
