package com.example.pablo.app2;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import hu.dcwatch.embla.nio.EmblaIoHandler;
import hu.dcwatch.embla.nio.EmblaProtocolFactory;
import hu.dcwatch.embla.nio.listener.ConnectionAdapter;
import hu.dcwatch.embla.protocol.adc.command.AdcCommand;
import hu.dcwatch.embla.protocol.adc.command.AdcCommandManager;
import hu.dcwatch.embla.protocol.adc.command.filter.AdcCommandHeaderFilter;
import hu.dcwatch.embla.protocol.adc.extension.AdcHashExtension;
import hu.dcwatch.embla.protocol.adc.extension.base.BaseContentProcessor;
import hu.dcwatch.embla.protocol.adc.extension.base.BaseHeaderProcessor;
import hu.dcwatch.embla.protocol.adc.extension.tigr.TigerHashExtension;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.example.echoserver.ssl.BogusSslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import javax.net.ssl.SSLContext;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class AdcClient {
    private AdcCommandManager adcCommandManager;
    private AdcHashExtension adcHashExtension;
    private BaseHeaderProcessor baseHeaderProcessor;
    private BaseContentProcessor baseContentProcessor;
    public String username;
    protected NioSocketConnector nioSocketConnector;
    protected String mySid;
    protected Map<String, AdcCommand> users;

    public AdcClient() {
        users = new HashMap<String, AdcCommand>();
    }

    public void send(String message){
        nioSocketConnector.broadcast(message);
    }

    public void connect(String host, int port) throws Exception {
        final String user = this.username;
        adcHashExtension = new TigerHashExtension();
        baseHeaderProcessor = new BaseHeaderProcessor();
        baseContentProcessor = new BaseContentProcessor();

        adcCommandManager = new AdcCommandManager();
        adcCommandManager.addHeaderProcessor(baseHeaderProcessor);
        adcCommandManager.addContentProcessor(baseContentProcessor);

        adcCommandManager.addCommandFilter(new AdcCommandHeaderFilter("I", "SID") {
            public boolean commandReceived(IoSession session, AdcCommand adcCommand) {
                adcCommandManager.processContent(adcCommand);
                mySid = adcCommand.getContent().getParameter(0);
                send(String.format("BINF %s ID%s PD%s NI%s SS%s SL%s", mySid, adcHashExtension.hash("LDAQQGNZNDTDOQJ57DNQZIZHOPAAYFWXDTYGE5P"), "LDAQQGNZNDTDOQJ57DNQZIZHOPAAYFWXDTYGE5P", user, "2000000", "15"));
                return true;
            }
        });
        adcCommandManager.addCommandFilter(new AdcCommandHeaderFilter("B", "INF") {
            public boolean commandReceived(IoSession session, AdcCommand adcCommand) {
                adcCommandManager.processContent(adcCommand);
                userConnected(adcCommand);
                return true;
            }
        });
        adcCommandManager.addCommandFilter(new AdcCommandHeaderFilter("I", "QUI") {
            public boolean commandReceived(IoSession session, AdcCommand adcCommand) {
                adcCommandManager.processContent(adcCommand);
                userDisconnected(adcCommand);
                return true;
            }
        });
        adcCommandManager.addCommandFilter(new AdcCommandHeaderFilter(null, "MSG") {
            public boolean commandReceived(IoSession session, AdcCommand adcCommand) {
                adcCommandManager.processContent(adcCommand);
                if(adcCommand.getHeader().getType().equals("B")){
                    chatReceived(adcCommand);
                }else if(adcCommand.getHeader().getType().equals("E")){

                    privateReceived(adcCommand);
                }
                return true;
            }
        });

        EmblaIoHandler emblaIoHandler = new EmblaIoHandler(adcCommandManager);
        emblaIoHandler.addConnectionListener(new ConnectionAdapter() {
            @Override
            public void connected(IoSession session) {
                session.write(String.format("HSUP AD%s AD%s", baseContentProcessor.getExtensionName(), adcHashExtension.getExtensionName()));
            }
        });

        nioSocketConnector = new NioSocketConnector();
        nioSocketConnector.getFilterChain().addLast(this.username, new ProtocolCodecFilter(new EmblaProtocolFactory("UTF-8", "\n", 64 * 1024)));
        //nioSocketConnector.getFilterChain().addLast("Logging", new LoggingFilter());
        nioSocketConnector.setHandler(emblaIoHandler);
    /*
        SSLContext sslContext = BogusSslContextFactory
                .getInstance(false);
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        nioSocketConnector.getFilterChain().addFirst("SSL", sslFilter);
    */
        nioSocketConnector.connect(new InetSocketAddress(host, port));

    }

    protected void userConnected(AdcCommand adcCommand){
        if(users.containsKey(adcCommand.getHeader().getSender())){
            AdcCommand user = users.get(adcCommand.getHeader().getSender());
            if(user != null){
                user.getContent().getNamedFields().putAll(adcCommand.getContent().getNamedFields());
            }
        }else{
            users.put(adcCommand.getHeader().getSender(), adcCommand);
        }
    }

    protected void userDisconnected(AdcCommand adcCommand){
        users.remove(adcCommand.getContent().getParameter(0));
    }

    protected void chatReceived(AdcCommand adcCommand){
    }

    protected void privateReceived(AdcCommand adcCommand){
    }
}