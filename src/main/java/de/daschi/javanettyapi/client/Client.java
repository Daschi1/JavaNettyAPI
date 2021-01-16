package de.daschi.javanettyapi.client;

import de.daschi.javanettyapi.core.Core;
import de.daschi.javanettyapi.core.JavaNettyAPIException;
import de.daschi.javanettyapi.packets.Packet;
import de.daschi.javanettyapi.packets.PacketDecoder;
import de.daschi.javanettyapi.packets.PacketEncoder;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientRegistered;
import de.daschi.javanettyapi.packets.client.PacketPlayOutClientUnregistered;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.util.UUID;

public class Client {
    private static Client client;

    public static Client getClient() {
        return Client.client;
    }

    private final String hostname;
    private final int port;
    private final UUID uuid;
    private final EventLoopGroup eventLoopGroup;
    private Channel channel;

    public Client(final String hostname, final int port, final int nThreads) {
        Client.client = this;
        this.hostname = hostname;
        this.port = port;
        this.uuid = UUID.randomUUID();
        this.eventLoopGroup = nThreads != 0 ? Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup(nThreads) : new NioEventLoopGroup(nThreads) : Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public void connect() {
        try {
            this.channel = new Bootstrap().group(this.eventLoopGroup).channel(Core.EPOLL_IS_AVAILABLE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer<>() {
                @Override

                protected void initChannel(final Channel channel) throws SSLException {
                    SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                    SSLEngine sslEngine = sslContext.newEngine(channel.alloc(), hostname, port);
                    sslEngine.setEnabledProtocols(new String[]{"TLSv.1.2"});
                    channel.pipeline().addLast(new SslHandler(sslEngine, false)).addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ClientSession());
                }
            }).connect(this.hostname, this.port).sync().channel();

            this.sendPacket(new PacketPlayOutClientRegistered(this.uuid));
        } catch (final InterruptedException exception) {
            throw new JavaNettyAPIException("Could not connect the client to '" + this.hostname + ":" + this.port + "'.", exception);
        }
    }

    public void disconnect() {
        this.sendPacket(new PacketPlayOutClientUnregistered(this.uuid));
        this.channel.closeFuture().syncUninterruptibly();
        this.eventLoopGroup.shutdownGracefully();
    }

    public void sendPacket(final Packet packet) {
        this.channel.writeAndFlush(packet, this.channel.voidPromise());
    }

    public void sendPacketAsync(final Packet packet) {
        this.eventLoopGroup.submit(() -> {
            this.channel.writeAndFlush(packet, this.channel.voidPromise());
        });
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EventLoopGroup getEventLoopGroup() {
        return this.eventLoopGroup;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
