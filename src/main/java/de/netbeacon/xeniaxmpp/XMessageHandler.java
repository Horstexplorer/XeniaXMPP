package de.netbeacon.xeniaxmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.parts.Resourcepart;

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
        if (msg.contains(nick)){
            //Cut nick out of input
            msg = msg.replace(nick, "").replace(nick, "");
            //Remove empty spaces
            msg = msg.trim();
        }

        //Check if it matches basic functions
        try{
            if(msg.equals("")){
                done = true;
                muc.sendMessage("Hm?");
            }
            if(msg.equals("hey") && !done){
                done = true;
                muc.sendMessage("Hii :3");
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
            if(msg.equals("info") && !done){
                done = true;
                muc.sendMessage("Perm:"+permlvl);
            }
        }catch(Exception e){
            System.err.println("XMH "+e);
        }finally{
            if(done){
                System.out.println("[REQ] OK");
            }else if(Boolean.parseBoolean(config.load("sys_usemodules"))){
                XModuleHandler xmh = new XModuleHandler(msg, permlvl, muc);
                if(xmh.load()){
                    System.out.println("[REQ] OK");
                }else{
                    System.out.println("[REQ] Ignored");
                }
            }else{
                System.out.println("[REQ] Ignored");
            }
        }
    }
}

