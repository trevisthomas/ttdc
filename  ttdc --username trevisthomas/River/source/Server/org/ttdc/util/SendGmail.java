package org.ttdc.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * This sucker sends mail using gmail. I had some trouble getting this to work because of the SSL stuff
 * It works with JavaMail 1.4.   Also, i had to remove the J2ee stuff that MyEclipse put into my project
 * to get it to run. They kept trying to inject 1.3 which broke everything. To fix, i just removed J2EE
 * I should have all of the jars in my project anyway. 
 * 
 * 
 * @author Trevis
 * 
 */
public class SendGmail {
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static String SMTP_AUTH_USER = "email";
    private static String SMTP_AUTH_PWD  = "pwd";

	public static enum ContentType {
		HTML, URL, TEXT
	}

	private static SendGmail me = null;

	private SendGmail() {
	}
	
	public static SendGmail getInstance(){
		if (me == null) {
			SMTP_AUTH_USER = ApplicationProperties.getProperty("SMTP_AUTH_USER");
			SMTP_AUTH_PWD = ApplicationProperties.getProperty("SMTP_AUTH_PWD");
			
			me = new SendGmail();
		}
		return me;
	}

	private InternetAddress[] parseEmailAddresses(String addressString) throws AddressException {
		InternetAddress[] addrArray = null;
		addrArray = InternetAddress.parse(addressString, false);
		return addrArray;
	}

	public static void sendMail(String content, ContentType type, String subject, String from, String to)
			throws BizException {
		getInstance().performSendMail(content, type, subject, from, to, null, null);
	}

	public static void sendMail(String content, ContentType type, String subject, String from, String to, String cc)
			throws BizException {
		getInstance().performSendMail(content, type, subject, from, to, cc, null);
	}

	public static void sendMail(String content, ContentType type, String subject, String from, String to, String cc,
			String bcc) throws BizException {
		getInstance().performSendMail(content, type, subject, from, to, cc, null);
	}

	/**
	 * Since this class does nothing else it seemed to make sense to just make
	 * this method private and and just perform all of the work internally with
	 * static accessors.
	 * 
	 * @param content
	 * @param type
	 * @param subject
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @throws BizException
	 */
	private void performSendMail(String content, ContentType type, String subject, String from, String to, String cc,
			String bcc) throws BizException {
		try {
			
			
			Properties props = new Properties();

			props.put("mail.transport.protocol", "smtps");
	        props.put("mail.smtps.host", SMTP_HOST_NAME);
	        props.put("mail.smtps.auth", "true");

	        Session mailSession = Session.getDefaultInstance(props);
	        
	        mailSession.setDebug(true);
	        
			Message msg = new MimeMessage(mailSession);
			msg.setFrom(parseEmailAddresses(from)[0]);
			msg.setReplyTo(parseEmailAddresses(from));

			msg.setRecipients(Message.RecipientType.TO, parseEmailAddresses(to));
			if (cc != null) {
				msg.setRecipients(Message.RecipientType.CC, parseEmailAddresses(cc));
			}
			if (bcc != null) {
				msg.setRecipients(Message.RecipientType.BCC, parseEmailAddresses(bcc));
			}
			msg.setSubject(subject);

			msg.setText(content); //Always add the text version of the content.  The MimeMessage will default to this
								  //if the recipient has html disabled.
			switch (type) {
				case HTML: {
					msg.setDataHandler(new DataHandler(new HTMLDataSource(content)));
					break;
				}
				case URL: {
					msg.setDataHandler(new DataHandler(new URLDataSource(new URL(content))));
					break;
				}
			}
			
			Transport transport = mailSession.getTransport();
			transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);

	        transport.sendMessage(msg,
	        		msg.getRecipients(Message.RecipientType.TO));
	        transport.close();
		} catch (AddressException e) {
			throw new BizException(e);
		} catch (MessagingException e) {
			throw new BizException(e);
		} catch (MalformedURLException e) {
			throw new BizException(e);
		} catch (Throwable t){
			throw new BizException(t);
		}
		
	}
	/*
     * Inner class to act as a JAF datasource to send HTML e-mail content
     * 
     * Grabbed from here
     * http://www.vipan.com/htdocs/javamail.html
     */
    static class HTMLDataSource implements DataSource {
        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        // Return html string in an InputStream.
        // A new stream must be returned each time.
        public InputStream getInputStream() throws IOException {
            if (html == null) throw new IOException("Null HTML");
            return new ByteArrayInputStream(html.getBytes());
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        public String getContentType() {
            return "text/html";
        }

        public String getName() {
            return "JAF text/html dataSource to send e-mail only";
        }
	} //End of class
}
