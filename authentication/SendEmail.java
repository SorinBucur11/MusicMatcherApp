package authentication;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {

    private static String USER_NAME = "musicmatcherapp";  // GMail user name (just the part before "@gmail.com")
    private static String PASSWORD = "cacealma"; // GMail password

    public static void sendFromGMail(String[] to, String pwd) {
    	String from = USER_NAME;
        String pass = PASSWORD;
        String subject = "Music Matcher password reset";
        String body = "\n"
        				+ "\t A new password has been generated to your account: " + pwd
        				+ "\n"
        				+ "\t You may now login using your new credentials. It is adviced to change "
        				+ "your password."
        				+ "\n\t You can do that from your user menu in the app."
        				+ "\n\n"
        				+ "-- Music Matcher";
    	
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i = 0; i < to.length; i++ ) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for( int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }
}
