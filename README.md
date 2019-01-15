# XeniaXMPP
#### Jabber/XMPP bot using Smack
<br></br>
> Current Version: 1.0.1.12 
### Configuration
All settings are stored in the file sys.conf. This file will be created automatically at first start
```
sys_activate=false                      // to disable everything, to make sure everything is set up
sys_autoreconnect=false                 // reconnect in case of connection loss
sys_usecmodule=false                    // activate priority extension
sys_usemodules=false                    // activate extensions

xmpp_domain=xmpp.domain.tld             // domain of the xmpp server
xmpp_host=xmpp.domain.tld               // hostname of the xmpp server(mostly equal to the domain)
xmpp_port=5222                          // port of the xmpp-server
xmpp_user=username                      // your username
xmpp_password=password                  // your password
xmpp_muc=room@conference                // the multiuserchat to join
xmpp_nickname=nickname                  // nickname
```
There is also a file for the configuration of user permission (auth.conf)
```
admin_users=admin1@xmpp.host.tld, admin2@xmpp.host.tld
priority_users=prior1@xmpp.host.tld, prior2@xmpp.host.tld
blocked_users=blocked1@xmpp.host.tld, blocked2@xmpp.host.tld

// Just change the value to username@xmpp.domain.de where needed
// Default permission level is 3 for admin_users, 2 for priority_users, 1 for default (everyone not listed here is default), 0 for blocked_user
```
### Commands
This list contains only the commands includes by default and their required permissions
```
empty --> Hm?                               // 0
hey --> Hii :3                              // 0
info --> displays your permission level     // 0
version --> displays the version of the bot // 2
kick <username> --> trys to kick user       // 2
restart --> restarts the bot                // 3
shutdown --> terminates the bot             // 3
```
The functionality of the bot can be extended with modules
### Modules
Create your own or use existing ones
##### Use existing ones:
Check out the /finishedmodules folder. Just take the jar's you want and place them in the /modules directory of your bot. Please make sure you enabled extensions in the config.
##### Create your own:
Its quite simple. You need to create something like this:
```
package tld.your.package.path;

import org.jivesoftware.smackx.muc.MultiUserChat;


public class YourModule {

    public String request(String msg, int perm, MultiUserChat muc){     // the bot will hand over the message, the permission level of the user and the multiuserchat to execute whatever you need.
        Boolean done = false;

        if(msg.contains("CONTAINS") && perm > 0){                       // this module will handle the request if the message contains CONTAINS and if the permission level is higher than 0 
            done = true;
            //do what you need to do
        }
        return done.toString();                                         // response to the bot if the module has handled the request. (it doesnt need to try other modules)
    }
}
```
It is necessary that the jar contains a manifest with the main-class attribute which needs to be set to the class where you have your request() function.</p>
Here is an overview of some functions that you could use.
```
muc.sendmessage(String text);                                   // sends text as message to muc
muc.kickParticipant(Resourcepart nickname, String reason);      // kick the user
```
[Smack Documentation](http://download.igniterealtime.org/smack/docs/latest/documentation/)
<p>Make sure to copy the necessary dependencies from build.gradle for smack.</p>
