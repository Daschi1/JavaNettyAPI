package de.daschi.javanettyapi.server;

import de.daschi.javanettyapi.core.Core;
import de.daschi.javanettyapi.core.JavaNettyAPIException;
import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketDecoder;
import de.daschi.javanettyapi.packets.PacketEncoder;
import de.daschi.javanettyapi.packets.server.PacketPlayOutClientDisconnect;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Server {

    private static Server server;

    public static Server getServer() {
        return Server.server;
    }

    private final int port;
    private final String hostname;
    private boolean shuttingDown;
    private final EventLoopGroup eventLoopGroup;
    private Channel channel;

    public Server(final String hostname, final int port, final int nThreads) {
        Server.server = this;
        this.port = port;
        this.hostname = hostname;
        this.shuttingDown = false;
        this.eventLoopGroup = nThreads != 0 ? Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup(nThreads) : new NioEventLoopGroup(nThreads) : Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public void connectWithOptions() {
        try {
            this.channel = new ServerBootstrap().group(this.eventLoopGroup).option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT).option(ChannelOption.AUTO_READ, true).channel(Core.EPOLL_IS_AVAILABLE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.AUTO_READ, true).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(final Channel channel) {
                    channel.pipeline().addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ServerSession());
                }
            }).bind(this.hostname, this.port).sync().channel();
        } catch (final InterruptedException exception) {
            throw new JavaNettyAPIException("Could not bind the server on '" + this.port + "'.", exception);
        }
    }

    public void connect() {
        try {
            this.channel = new ServerBootstrap().group(this.eventLoopGroup).channel(Core.EPOLL_IS_AVAILABLE ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(final Channel channel) {
                    channel.pipeline().addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ServerSession());
                }
            }).bind(this.hostname, this.port).sync().channel();
        } catch (final InterruptedException exception) {
            throw new JavaNettyAPIException("Could not bind the server on '" + this.port + "'.", exception);
        }
    }


    public void disconnect() {
        CompletableFuture.runAsync(() -> {
            this.shuttingDown = true;
            this.disconnectAllClients();
            this.channel.closeFuture().awaitUninterruptibly();
            this.eventLoopGroup.shutdownGracefully();
        }, this.getEventLoopGroup());
    }

    public void sendPacket(final UUID uuid, final Packet packet) {
        ServerSession.getChannels().get(uuid).writeAndFlush(packet, ServerSession.getChannels().get(uuid).voidPromise());
    }

    public CompletableFuture<Void> sendPacketAsync(final UUID uuid, final Packet packet) {
        return CompletableFuture.runAsync(() -> ServerSession.getChannels().get(uuid).writeAndFlush(packet, ServerSession.getChannels().get(uuid).voidPromise()), this.getEventLoopGroup());
    }

    public void sendPacketToAll(final Packet packet) {
        ServerSession.getChannels().forEach((uuid, channel) -> this.sendPacket(uuid, packet));
    }

    public CompletableFuture<Void> sendPacketToAllAsync(final Packet packet) {
        return CompletableFuture.runAsync(() -> ServerSession.getChannels().forEach((uuid, channel) -> this.sendPacket(uuid, packet)), this.getEventLoopGroup());
    }

    public void disconnectClient(final UUID uuid) {
        ServerSession.getChannels().get(uuid).writeAndFlush(new PacketPlayOutClientDisconnect(), ServerSession.getChannels().get(uuid).voidPromise());
    }

    public void disconnectAllClients() {
        final Map<UUID, Channel> uuidChannelMap = ServerSession.getChannels();
        if (!uuidChannelMap.isEmpty()) {
            uuidChannelMap.forEach((uuid, channel) -> this.disconnectClient(uuid));
        }
    }

    public String getHostname() {
        return this.hostname;
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
