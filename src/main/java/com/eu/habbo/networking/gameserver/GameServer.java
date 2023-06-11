package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.networking.Server;
import com.eu.habbo.networking.gameserver.codec.WebSocketCodec;
import com.eu.habbo.networking.gameserver.decoders.*;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageEncoder;
import com.eu.habbo.networking.gameserver.encoders.GameServerMessageLogger;
import com.eu.habbo.networking.gameserver.handlers.CustomHTTPHandler;
import com.eu.habbo.networking.gameserver.handlers.IdleTimeoutHandler;
import com.eu.habbo.networking.gameserver.ssl.SSLCertificateLoader;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.users.UserGetIPAddressEvent;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.PacketManager;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;

@Getter
public class GameServer extends Server implements EventListener {
    private final PacketManager packetManager;
    private final GameClientManager gameClientManager;

    public static final AttributeKey<String> WS_IP = AttributeKey.valueOf("WS_IP");

    private static final int MAX_FRAME_SIZE = 500000;

    private final SslContext context;
    private final boolean isSSL;
    private final WebSocketServerProtocolConfig config;

    public GameServer(String host, int port) throws Exception {
        super("Game Server", host, port, Emulator.getConfig().getInt("ws.nitro.bossgroup.threads"), Emulator.getConfig().getInt("ws.nitro.workergroup.threads"));
        this.packetManager = new PacketManager();
        this.gameClientManager = new GameClientManager();
        this.context = SSLCertificateLoader.getContext();
        this.isSSL = context != null;
        this.config = WebSocketServerProtocolConfig.newBuilder()
            .websocketPath("/")
            .checkStartsWith(true)
            .maxFramePayloadLength(MAX_FRAME_SIZE)
            .build();
    }

    @Override
    public void initializePipeline() {
        super.initializePipeline();

        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("logger", new LoggingHandler());

                if(isSSL) {
                    ch.pipeline().addLast(context.newHandler(ch.alloc()));
                }
                ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                ch.pipeline().addLast("objectAggregator", new HttpObjectAggregator(MAX_FRAME_SIZE));
                ch.pipeline().addLast("customHttpHandler", new CustomHTTPHandler());
                ch.pipeline().addLast("protocolHandler", new WebSocketServerProtocolHandler(config));
                ch.pipeline().addLast("websocketCodec", new WebSocketCodec());

                // Decoders.
                ch.pipeline().addLast(new GamePolicyDecoder());
                ch.pipeline().addLast(new GameByteFrameDecoder());
                ch.pipeline().addLast(new GameByteDecoder());

                if (PacketManager.DEBUG_SHOW_PACKETS) {
                    ch.pipeline().addLast(new GameClientMessageLogger());
                }

                ch.pipeline().addLast("idleEventHandler", new IdleTimeoutHandler(30, 60));
                ch.pipeline().addLast(new GameMessageRateLimit());
                ch.pipeline().addLast(new GameMessageHandler());

                // Encoders.
                ch.pipeline().addLast(new GameServerMessageEncoder());

                if (PacketManager.DEBUG_SHOW_PACKETS) {
                    ch.pipeline().addLast(new GameServerMessageLogger());
                }
            }
        });
    }

    @EventHandler
    public void onUserGetIPEvent(UserGetIPAddressEvent e) {
        Channel channel = e.habbo.getClient().getChannel();
        if(channel != null && channel.hasAttr(GameServer.WS_IP)) {
            String ip = channel.attr(GameServer.WS_IP).get();
            if(!ip.isEmpty()) {
                e.setUpdatedIp(ip);
            }
        }
    }
}