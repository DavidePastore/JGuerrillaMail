/**
 * 
 */
package com.guerrillamail.www;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * EMail.
 * @author <a reef="https://github.com/DavidePastore">DavidePastore</a>
 *
 */
public class EMail {
	
	private String recipient;
	private long timestamp;
	private String date;
	private boolean read;
	private String from;
	private int sourceId;
	private String excerpt;
	private int att;
	private String subject;
	private String contentType;
	private int sourceMailID;
	private int id;
	private String body;
	
	public EMail(JSONObject jSonObject) throws JSONException{
		System.out.println("jsonobject: "+jSonObject);
		recipient = jSonObject.getString("mail_recipient");
		timestamp = jSonObject.getLong("mail_timestamp");
		date = jSonObject.getString("mail_date");
		read = Utils.fromIntToBoolean(jSonObject.getInt("mail_read"));
		from = jSonObject.getString("mail_from");
		sourceId = jSonObject.getInt("source_id");
		excerpt = jSonObject.getString("mail_excerpt");
		att = jSonObject.getInt("att");
		subject = jSonObject.getString("mail_subject");
		contentType = jSonObject.getString("content_type");
		sourceMailID = jSonObject.getInt("source_mail_id");
		id = jSonObject.getInt("mail_id");
	}
	
	
	/* GET METHODS */
	
	/**
	 * Get the mail Id.
	 * @return the mail id of this email.
	 */
	public int getId(){
		return this.id;
	}
	
	
	/* SET METHODS */
	
	/**
	 * Set the body of the email.
	 * @param body the body of the EMail.
	 */
	public void setBody(String body){
		this.body = body;
	}
}
