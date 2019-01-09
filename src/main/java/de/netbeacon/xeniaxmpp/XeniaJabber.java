package de.netbeacon.xeniaxmpp;

public class XeniaJabber {

    public static void main(String[] args) {


        //Start
        System.out.println("----------[XeniaJabber]----------");

        //Get config ready
        Config config = new Config();
        //get modules ready
        new XModuleHandler("", 0, null);
        if (Boolean.parseBoolean(config.load("sys_activate"))) {

            //Prepare
            XMPPNET xmpp = new XMPPNET();
            //Connect
            xmpp.connect();
            //Join
            xmpp.joinchatroom();
            //just keep everything alive
            try{
                while(true){
                    //do nothing
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            System.out.println("Deactivated by config");
        }

    }

}
