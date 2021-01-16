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
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.UUID;

@SuppressWarnings("rawtypes")
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
    private final boolean ssl;
    private SslContext sslContext;

    public Client(final String hostname, final int port, final boolean ssl, final int nThreads) {
        Client.client = this;
        this.hostname = hostname;
        this.port = port;
        this.uuid = UUID.randomUUID();
        this.ssl = ssl;
        this.eventLoopGroup = nThreads != 0 ? Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup(nThreads) : new NioEventLoopGroup(nThreads) : Core.EPOLL_IS_AVAILABLE ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    public void connect() {
        if (this.ssl) {
            this.setSslContext();
            try {
                this.channel = new Bootstrap().group(this.eventLoopGroup).channel(Core.EPOLL_IS_AVAILABLE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(final Channel channel) {
                        channel.pipeline().addLast(Client.this.sslContext.newHandler(channel.alloc(), Client.this.hostname, Client.this.port)).addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ClientSession());
                    }
                }).connect(this.hostname, this.port).sync().channel();
                this.sendPacket(new PacketPlayOutClientRegistered(this.uuid));
            } catch (final InterruptedException exception) {
                throw new JavaNettyAPIException("Could not connect the client to '" + this.hostname + ":" + this.port + "'.", exception);
            }
        } else {
            try {
                this.channel = new Bootstrap().group(this.eventLoopGroup).channel(Core.EPOLL_IS_AVAILABLE ? EpollSocketChannel.class : NioSocketChannel.class).handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(final Channel channel) {
                        channel.pipeline().addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new ClientSession());
                    }
                }).connect(this.hostname, this.port).sync().channel();
                this.sendPacket(new PacketPlayOutClientRegistered(this.uuid));
            } catch (final InterruptedException exception) {
                throw new JavaNettyAPIException("Could not connect the client to '" + this.hostname + ":" + this.port + "'.", exception);
            }
        }
    }

    private void setSslContext() {
        try {
            final SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate();
            this.sslContext = SslContext.newClientContext(selfSignedCertificate.certificate());
        } catch (final CertificateException | SSLException e) {
            e.printStackTrace();
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

    public boolean isSsl() {
        return this.ssl;
    }

    public SslContext getSslContext() {
        return this.sslContext;
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
