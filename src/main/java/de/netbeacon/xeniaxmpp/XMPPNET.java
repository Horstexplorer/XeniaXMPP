package de.netbeacon.xeniaxmpp;


import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

class XMPPNET {

    private AbstractXMPPConnection connection;
    private MultiUserChat muc;
    private String user;
    private String pass;
    private String domain;
    private String host;
    private String room;
    private String nick;
    private int port;
    private Config config;

    XMPPNET(){
        config = new Config();
        user = config.load("xmpp_user");
        pass = config.load("xmpp_password");
        domain = config.load("xmpp_domain");
        host = config.load("xmpp_host");
        port = Integer.parseInt(config.load("xmpp_port"));
        room = config.load("xmpp_muc");
        nick = config.load("xmpp_nickname");
    }
    void connect(){
        System.out.println("Connecting to "+domain+" as "+user);
        try{
            // Create a connection to the jabber.org server on a specific port.
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(user, pass)
                    .setXmppDomain(domain)
                    .setHost(host)
                    .setPort(port)
                    .setHostnameVerifier((hostname, session) -> true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                    .build();
            connection = new XMPPTCPConnection(config);
            connection.connect();
            connection.login();
        }catch(Exception e){
            System.err.println("XN "+e);
            System.exit(1);
        }finally {
            System.out.println("Connected");
        }
    }

    void joinchatroom(){
        System.out.println("Joining MUC "+room);
        try{
            MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(connection);

            EntityBareJid jid = JidCreate.entityBareFrom(room+"."+host);
            muc = mucm.getMultiUserChat(jid);
            Resourcepart nickname = Resourcepart.from(nick);
            muc.join(nickname);
            Thread.sleep(500);
            System.out.println("Adding message listener");
            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    if (message.getBody() != null) {
                        //let the fun begin.
                        String body = message.getBody();
                        String from = message.getFrom().toString();

                        from = from.replace(config.load("xmpp_muc") + "." + config.load("xmpp_domain"), "").replace("/", "");

                        try {
                            //Check if the request is for the bot and not from the bot
                            if (!from.toLowerCase().contains(nick)) {
                                System.out.println("[REQ]<"+from+">: "+body);
                                //Check if user auth lvl is enough to communicate with the bot
                                XChatUserAuth xcua = new XChatUserAuth();
                                int permlvl = xcua.getpermlvl(from);

                                if (permlvl >= 1) {
                                    Thread mh = new Thread(new XMessageHandler(connection, muc, permlvl, body));
                                    mh.start();
                                }
                                // Should add db cnn to log
                            }
                        } catch (Exception e) {
                            System.err.println("XN "+e);
                        }
                    }
                }
            });
        }catch(Exception e){
            System.err.println("XN "+e);
            System.exit(1);
        }finally{
            System.out.println("Done");
            System.out.println("---------------------------------");
        }
    }
}
