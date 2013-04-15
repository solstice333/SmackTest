package jabber.smack.test;

import java.util.Scanner;

import jabber.smack.api.JabberSmackAPI;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class PacketCollectorAndPacketListenerTest {

   public static void main(String[] args) throws XMPPException,
         InterruptedException {
      Scanner s = new Scanner(System.in);

      // connect
      JabberSmackAPI j = new JabberSmackAPI();
      XMPPConnection.DEBUG_ENABLED = true;
      XMPPConnection connection = j.login("openadrventest@gmail.com",
            "openadrtest");

      // create filter
      PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class),
            new FromContainsFilter("openadrvtntest@gmail.com"));

      // instantiate PacketCollector using filter (careful when using listener with collector. 
      // Collector waits till a dequeue while listener is instant event driven)
      PacketCollector myCollector = connection.createPacketCollector(filter);

      // attach listener to connection (careful when using listener with collector. 
      // Collector waits till a dequeue while listener is instant event driven.)
      // PacketListener myListener = (PacketListener) j;
      // connection.addPacketListener(myListener, filter);

      // display buddy list, select buddy, open message input console
      j.displayBuddyList();
      System.out.println("------------------------");
      System.out.println("Who do you want to talk to?"
            + "\nType contacts full email address. "
            + "\nType 'refresh' to refresh list and presence status:"
            + "\nType 'quit()' to quit");

      String to = "";
      boolean valid = false;
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

      // message input console and PacketCollector test
      String msg = "";
      while (true) {
         // PacketCollector tests
         System.out.println("blocking...");

         Packet packet = myCollector.pollResult();
         while (packet != null) {
            try {
               System.out.println("packet.getFrom(): " + packet.getFrom());
            }
            catch (NullPointerException npe) {
               System.out
                     .println("null pointer exception received for packet.getFrom() ");
            }

            try {
               System.out.println("packet.toXML(): " + packet.toXML());
            }
            catch (NullPointerException npe) {
               System.out
                     .println("null pointer exception received for packet.toXML() ");
            }
            System.out.println("\n");

            packet = myCollector.nextResult(2000L);
            if (packet != null)
               Thread.sleep(2000L);
         }

         System.out.println("unblocked...");

         // send message after unblocking
         msg = s.nextLine();
         if (msg.equals("quit()"))
            break;
         j.sendMessage(msg, to);
      }

      j.disconnect();
      System.out.println("Testing done. Quitting...");
      System.exit(0);
   }

}
