package de.netbeacon.xeniaxmpp;

public class XeniaJabber {

    public static void main(String[] args) {


        //Start
        System.out.println("----------[XeniaJabber]----------");

        //Get config ready
        Config config = new Config();

        if (Boolean.parseBoolean(config.load("sys_activate"))) {

            //Prepare
            XMPPNET xmpp = new XMPPNET();
            //Connect
            xmpp.connect();
            //Join
            xmpp.joinchatroom();
            //just keep everything alive
            while(true){
            }

        }else{
            System.out.println("Deactivated by config.");
        }

    }

}
