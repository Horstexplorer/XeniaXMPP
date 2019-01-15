package de.netbeacon.xeniaxmpp;

import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

class XCModuleHandler {

    //for a module with extended features

    private String msg;
    private int permlvl;
    private MultiUserChat muc;
    private Boolean loadmod = false;

    XCModuleHandler(String message, int permlvll, MultiUserChat mucc){

        //set vars
        msg = message;
        permlvl = permlvll;
        muc = mucc;

        //Check if cmod exists
        File cmod = new File("./cmod.jar");
        if (cmod.exists()) {
            loadmod = true;
        }

    }

    Boolean load(){
        Boolean handled = false;

        if(loadmod){
            //do some work
            try{
                //Get main class from file
                JarFile jfile = new JarFile("./cmod.jar");
                Manifest mf = jfile.getManifest();
                Attributes atr = mf.getMainAttributes();
                String maincp = atr.getValue("Main-Class");

                //Do magic
                URL[] clu = new URL[]{new URL("file:./cmod.jar")};
                URLClassLoader child = new URLClassLoader(clu, this.getClass().getClassLoader());
                Class<?> classToLoad = Class.forName(maincp, true, child);
                Method method = classToLoad.getDeclaredMethod("request", String.class, int.class, MultiUserChat.class); // MessageReceivedEvent; Message; Perm_lvl to module, MUC for more fun :D
                Object instance = classToLoad.getConstructor().newInstance();

                Object resul = method.invoke(instance, msg, permlvl, muc);

                if(resul != null){
                    handled = Boolean.parseBoolean(resul.toString());
                }

            }catch (Exception e){
                System.out.println("[ERROR] "+e);
            }
        }

        return handled;
    }
}
