package de.netbeacon.xeniaxmpp;


import org.jivesoftware.smackx.muc.MultiUserChat;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

class XModuleHandler {

    private String[] modules = new String[128];
    private boolean finished = false;
    private String msg;
    private int permlvl;
    private MultiUserChat muc;
    private boolean modex = false;

    XModuleHandler(String message, int permlvll, MultiUserChat mucc){
        //Setvar
        msg = message;
        permlvl = permlvll;
        muc = mucc;
        modules[0] = "";

        //Check if dir exists
        File directory = new File("./modules/");
        if (!directory.exists()) {
            directory.mkdir();
        }
        //Check if module exist, if load into array
        File[] listOfFiles = directory.listFiles();
        if(listOfFiles != null && listOfFiles.length > 0){
            int x = 0;
            for (File f: listOfFiles){
                if(f.getName().matches(".*jar$")){
                    x++;
                }
            }
            if(x != 0){
                modex = true;
                int y = 0;
                modules = new String[x];
                for (File f: listOfFiles){
                    if(f.getName().matches(".*jar$")){
                        modules[y] = f.getName();
                        y++;
                    }
                }
            }
        }
    }

    boolean load(){
        boolean handled = false;
        if(modex){
            try{
                int x = 0;
                while(x <= modules.length-1 && !finished){

                    String filename = modules[x].replace(".jar", "");

                    //Get main class from file
                    JarFile jfile = new JarFile("./modules/"+modules[x]);
                    Manifest mf = jfile.getManifest();
                    Attributes atr = mf.getMainAttributes();
                    String maincp = atr.getValue("Main-Class");

                    //Do magic
                    URL[] clu = new URL[]{new URL("file:./modules/"+modules[x])};
                    URLClassLoader child = new URLClassLoader(clu, this.getClass().getClassLoader());
                    Class<?> classToLoad = Class.forName(maincp, true, child);
                    Method method = classToLoad.getDeclaredMethod("request", String.class, int.class); // MessageReceivedEvent; Message; Perm_lvl to module
                    Object instance = classToLoad.getConstructor().newInstance();

                    Object result = method.invoke(instance, msg, permlvl);

                    if(result != null){
                        String[] results = result.toString().split(", ");
                        if(results[0].equals("true") && results.length == 2){
                            finished = true;
                            handled = true;
                            muc.sendMessage(results[1]);
                        }else{
                            finished = false;
                        }
                    }else{
                        finished = false;
                    }

                    x++;
                }
            }catch(Exception e){
                System.err.println("XModH "+e);
            }
        }
        return handled;
    }
}