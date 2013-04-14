package jabber.smack.test;

import java.util.Scanner;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import jabber.smack.api.JabberSmackAPI;

public class ChatClientApp {

   public static void main(String[] args) throws XMPPException,
         InterruptedException {
      JabberSmackAPI j = new JabberSmackAPI();
      
      @SuppressWarnings("resource")
      Scanner s = new Scanner(System.in);
      
      String msg = "";
      String to = null;
      boolean valid = false, repeat = true;

      //XMPPConnection.DEBUG_ENABLED = true;

      j.login("knavero@gmail.com", "Hs200oo,");

      while (repeat) {
         j.displayBuddyList();
         System.out.println("------------------------");
         System.out.println("Who do you want to talk to?"
               + "\nType contacts full email address. "
               + "\nType 'refresh' to refresh list and presence status:"
               + "\nType 'quit()' to quit");

         while (!valid) {
            to = s.nextLine();
            valid = true;

            if (to.equals("quit()")) {
               System.out.println("Quitting...");
               System.exit(0);
            }

            if (to.equals("refresh")) {
               valid = false;
               j.displayBuddyList();
               System.out.println("------------------------");
               System.out.println("Who do you want to talk to?"
                     + "\nType contacts full email address. "
                     + "\nType 'refresh' to refresh list and presence status:"
                     + "\nType 'quit()' to quit");
            }
            else if (!j.checkIfExists(to)) {
               valid = false;
               System.out.println("Contact does not exist. Try again: ");
            }
         }

         System.out.println("---------------------------");
         System.out.println("All messages will be sent to: " + to + ", " 
               + j.getConnection().getRoster().getEntry(to).getName()
               + "\nType 'quit()' to quit"
               + "\nEnter the message in the console: ");
         System.out.println("---------------------------");

         while (true) {
            msg = s.nextLine();
            if (msg.equals("quit()"))
               break;
            j.sendMessage(msg, to);
         }

         char ans = 0;
         boolean invalidChoice = true;
         System.out.println("Choose different buddy to talk to? (y/n):");
         ans = s.nextLine().charAt(0);
         
         while (invalidChoice) {
            invalidChoice = false;
            switch (ans) {
            case 'y':
               repeat = true;
               valid = false;
               break;
            case 'n':
               repeat = false;
               break;
            default:
               System.out.println("Invalid choice");
               invalidChoice = true;
            }
         }
      }

      j.disconnect();
      System.out.println("Quitting...");
      System.exit(0);
   }

}
