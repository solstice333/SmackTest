package jabber.smack.api;

import java.io.File;
import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

public class JabberSmackAPI implements MessageListener, PacketListener {
   private XMPPConnection conn;
   private FileTransferManager ftm;

   public XMPPConnection login(String username, String password)
         throws XMPPException {
      ConnectionConfiguration config = new ConnectionConfiguration(
            "talk.google.com", 5222, "Private");
      conn = new XMPPConnection(config);
      conn.connect();
      conn.login(username, password);

      return conn;
   }

   public XMPPConnection getConnection() {
      return conn;
   }

   public void sendMessage(String msg, String to) throws XMPPException {
      Chat chat = conn.getChatManager().createChat(to, this);
      chat.sendMessage(msg);
   }

   public void displayBuddyList() throws InterruptedException {
      Roster roster = conn.getRoster();
      Collection<RosterEntry> buddies = roster.getEntries();
      int count = countBuddies();

      if (count == 0) {
         Thread.sleep(1000);
         displayBuddyList();
      }

      else {
         System.out.println(count + " buddies:");
         for (RosterEntry entry : buddies) {
            if (roster.getPresence(entry.getUser()).isAvailable()) {
               System.out.println("User: " + entry.getUser() + "\nName: "
                     + entry.getName() + "\nPresence: "
                     + roster.getPresence(entry.getUser()));
               System.out.println();
            }
         }
      }
   }

   public int countBuddies() {
      int count = 0;
      Roster roster = conn.getRoster();
      Collection<RosterEntry> buddies = roster.getEntries();

      for (RosterEntry entry : buddies) {
         if (roster.getPresence(entry.getUser()).isAvailable())
            count++;
      }

      return count;
   }

   public boolean checkIfExists(String user) {
      boolean check = false;
      Roster roster = conn.getRoster();
      Collection<RosterEntry> buddies = roster.getEntries();
      for (RosterEntry entry : buddies) {
         if (entry.getUser().equals(user))
            check = true;
      }
      return check;
   }

   public void setConnection(XMPPConnection connection) {
      conn = connection;
      ftm = new FileTransferManager(conn);
   }

   public void fileTransfer(String filename, String destination)
         throws XMPPException, InterruptedException {
      FileTransferNegotiator.setServiceEnabled(conn, true);
      OutgoingFileTransfer transfer = ftm
            .createOutgoingFileTransfer(destination);
      transfer.sendFile(new File(filename), filename);

      while (transfer.getProgress() != 1) {
         Thread.sleep(1000);
         System.out.println("Progress: " + transfer.getProgress()
               + "\nStatus: " + transfer.getStatus() + "\nException: "
               + transfer.getException() + "\nError: " + transfer.getError());
      }
      System.out.println("Progress: " + transfer.getProgress() + "\nStatus: "
            + transfer.getStatus() + "\nException: " + transfer.getException()
            + "\nError: " + transfer.getError());

      if (transfer.isDone())
         System.out.println("File transfer is done");
   }

   public void disconnect() {
      if (conn != null && conn.isConnected())
         conn.disconnect();
   }

   @Override
   public void processMessage(Chat chat, Message message) {
      if (message.getType() == Message.Type.chat && message.getBody() != null)
         System.out.println(chat.getParticipant() + " says: "
               + message.getBody());
   }

   @Override
   public void processPacket(Packet packet) {
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
      
      try {
         Thread.sleep(2000L);
      }
      catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
