package de.netbeacon.xeniaxmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class XMessageHandler implements Runnable{

    private AbstractXMPPConnection connection;
    private MultiUserChat muc;
    private Config config;
    private String msg;
    private int permlvl;

    private String nick;
    private String version;


    XMessageHandler(AbstractXMPPConnection c, MultiUserChat m, int p, String body){

        // Work exec.
        connection = c;
        muc = m;
        config = new Config();
        permlvl = p;
        msg = body;

        // Config stuff
        nick = config.load("xmpp_nickname");
        version = config.getversion();
    }

    public void run(){
        boolean done = false;
        //remove capit. letters
        msg = msg.toLowerCase();
        //Cut nick out of input
        msg = msg.replace(nick+":", "").replace(nick, "");
        //Remove empty spaces
        msg = msg.trim();

        //Check if it is internal
        try{
            if(msg.equals("")){
                done = true;
                muc.sendMessage("Hm?");
            }
            if(msg.equals("hey") && !done){
                done = true;
                muc.sendMessage("Hii :3");
            }
            if(msg.equals("status") && !done && permlvl == 3){
                done = true;
                muc.sendMessage("Version["+version+"]");
                muc.sendMessage("activemsghandling "+config.load("sys_activemsghandling"));
                muc.sendMessage("usemodules "+config.load("sys_usemodules"));
            }

            if(msg.equals("restart") && !done && permlvl == 3){
                done = true;
                muc.sendMessage("ok");
                muc.leave();
                connection.disconnect();
                System.out.println("Disconnected");
                XMPPNET xmpp = new XMPPNET();
                xmpp.connect();
                xmpp.joinchatroom();
            }
            if(msg.equals("shutdown") && !done && permlvl == 3){
                done = true;
                muc.sendMessage("Bye :3");
                muc.leave();
                connection.disconnect();
                System.out.println("Disconnected");
                System.out.println("Shutdown");
                System.exit(1);
            }
            if(msg.contains("kick ") && !done && permlvl >= 2){
                done = true;
                msg = msg.replace("kick", "");
                msg = msg.trim();
                if(!msg.equals("")){
                    Resourcepart kickuser = Resourcepart.from(msg);
                    muc.kickParticipant(kickuser, "");
                }
            }
            if (msg.equals("ping")){
                done = true;
                muc.sendMessage("pong?");
            }
            if(msg.contains("ping(") && msg.contains(")") && !done && permlvl >= 1){
                done = true;
                boolean fail = false;
                msg = msg.substring(msg.indexOf("ping(") + 5);
                msg = msg.substring(0, msg.indexOf(")"));
                String host = msg.substring(0,msg.indexOf(":"));
                int port = Integer.parseInt(msg.substring(msg.indexOf(":")+1));
                if(host.matches("^192\\.168\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))$")){
                    fail = true;
                    muc.sendMessage("no pong");
                }
                if(!msg.equals("") && msg.contains(":") && !fail) {
                    try (Socket socket = new Socket()) {
                        socket.connect(new InetSocketAddress(host, port), 2000);
                        muc.sendMessage("pong");
                    } catch (IOException e) {
                        muc.sendMessage("no pong");
                    }
                }
            }
            if(msg.equals("info") && !done){
                done = true;
                muc.sendMessage("Perm:"+permlvl);
            }
        }catch(Exception e){
            System.err.println("XMH"+e);
        }finally{
            if(!done && Boolean.parseBoolean(config.load("sys_usemodules"))){
                new XModuleHandler(msg, permlvl, muc);
            }
        }
    }
}

