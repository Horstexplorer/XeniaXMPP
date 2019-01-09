package de.netbeacon.xeniaxmpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

class Config {

    Config() {
        //Check if config file exist
        File configfile = new File("sys.conf");
        if (!configfile.exists()) {
            //Create the file
            createconfigfile();
        }
    }

    private void createconfigfile() {

        Properties prop = new Properties();

        try {

            prop.setProperty("xmpp_host", "xmpp.domain.tld");
            prop.setProperty("xmpp_domain", "xmpp.domain.tld");
            prop.setProperty("xmpp_port", "5222");
            prop.setProperty("xmpp_user", "username");
            prop.setProperty("xmpp_nickname", "nickname");
            prop.setProperty("xmpp_password", "password");
            prop.setProperty("xmpp_muc", "chat@conference");

            prop.setProperty("sys_autoreconnect", "false");
            prop.setProperty("sys_activate", "false");
            prop.setProperty("sys_usemodules", "false");


            prop.store(new FileOutputStream("sys.conf"), null);
        } catch (Exception e) {
            System.err.println("XC "+e);
            System.exit(1);
        }

    }

    String load(String value) {

        Properties prop = new Properties();
        InputStream input;
        String result = "";

        try {
            input = new FileInputStream("sys.conf");
            prop.load(input);
            result = prop.getProperty(value);
        } catch (Exception e) {
            System.err.println("XC "+e);
        }

        return result;

    }

    void writevalue(String value, String input){
        //not used atm
        Properties prop = new Properties();
        FileOutputStream out;

        try {
            FileInputStream in = new FileInputStream("sys.conf");
            prop.load(in);
            in.close();
            out = new FileOutputStream("sys.conf");
            prop.setProperty(value, input);
            prop.store(out, null);
            out.close();
        } catch (Exception e) {
            System.err.println("XC "+e);
        }
    }

    //Version
    String getversion() {
        String version = "1.0.1.9";
        String build = "00453";
        String release = "r";

        return version+"-"+build+"-"+release;
    }
}
