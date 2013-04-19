package com.jda.bsnet
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

import sun.misc.BASE64Encoder

import com.jda.bsnet.model.User
import com.jda.bsnet.rest.BsnetDatabase
import com.mongodb.BasicDBObject


class BsnetUtils {


	static String encrypt(String plaintext) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(plaintext.getBytes("UTF-8")); //step 3
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} //step 2
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] raw = md.digest() //step 4
		String hash = (new BASE64Encoder()).encode(raw); //step 5
		return hash; //step 6
	}

	static String getAdminMailId(String orgName) {
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("orgName", orgName));
		obj.add(new BasicDBObject("orgAdmin", true));
		andQuery.put('$and', obj);
		User user = BsnetDatabase.getInstance().getJacksonDBCollection(User.class).findOne(andQuery)
		return user.emailId
	}


	static boolean sendMail(String toAddresses,String subject,String body)
	{
		boolean status=true;
		Properties bsnetProp = BsnetDatabase.getInstance().getBsnetServerConfig()
		String from = bsnetProp.get("mail.smtp.from").toString();
		String host = bsnetProp.get("mail.smtp.host").toString();
		String port= bsnetProp.get("mail.smtp.port").toString();
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		Session session = Session.getDefaultInstance(properties);
		session.setDebug(Boolean.parseBoolean(bsnetProp.get("mail.smtp.session.debug").toString()));
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(toAddresses));
			message.setSubject(subject);
			Transport.send(message);
		}catch (MessagingException e) {
			throw e;
		}
		return status;
	}
}


