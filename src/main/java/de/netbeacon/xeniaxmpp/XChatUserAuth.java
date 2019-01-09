package de.netbeacon.xeniaxmpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

class XChatUserAuth {

    XChatUserAuth(){
        //Check if config file exist
        File auth = new File("auth.conf");
        if (!auth.exists()) {
            //Create the file
            createauthfile();
        }
    }
    private void createauthfile(){
        Properties prop = new Properties();
        try {

            prop.setProperty("admin_users", "admin1@xmpp.host.tld, admin2@xmpp.host.tld");
            prop.setProperty("priority_users", "prior1@xmpp.host.tld, prior2@xmpp.host.tld");
            prop.setProperty("blocked_users", "blocked1@xmpp.host.tld, blocked2@xmpp.host.tld");

            prop.store(new FileOutputStream("auth.conf"), null);
        }catch(Exception e) {
            System.err.println("[ERROR] "+e);
            System.exit(1);
        }
    }
    private String load(String value) {

        Properties prop = new Properties();
        InputStream input;
        String result = "";

        try {
            input = new FileInputStream("auth.conf");
            prop.load(input);
            result = prop.getProperty(value);
        }catch(Exception e) {
            System.err.println("[ERROR] "+e);
            System.exit(1);
        }

        return result;

    }


    int getpermlvl(String user){
        int permlvl = 1;
        boolean found = false;
        int x = 0;


        try{
            //Get all users from the config
            String[] admin = load("admin_users").split(", ");
            String[] prior = load("priority_users").split(", ");
            String[] block = load("blocked_users").split(", ");
            //Check if user in there

            while(x <= admin.length-1){
                if(admin[x].equals(user)){
                    permlvl = 3;
                    found = true;
                    break;
                }
                x++;
            }
            x = 0;
            while(x <= prior.length-1 && !found){
                if(prior[x].equals(user)){
                    permlvl = 2;
                    found = true;
                    break;
                }
                x++;
            }
            x = 0;
            while(x <= block.length-1 && !found){
                if(block[x].equals(user)){
                    permlvl = 0; //default user permlvl = 1
                    break;
                }
                x++;
            }
        }catch(Exception e){
            System.err.println("XCUA"+ e);
        }


        return permlvl;
    }
}
