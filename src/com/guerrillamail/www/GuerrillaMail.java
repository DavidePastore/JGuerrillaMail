/**
 * 
 */
package com.guerrillamail.www;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface with guerrilla mail site.
 * @author <a reef="https://github.com/DavidePastore">DavidePastore</a>
 * @url documentation: https://docs.google.com/document/d/1Qw5KQP1j57BPTDmms5nspe-QAjNEsNg8cQHpAAycYNM/edit?hl=en
 */
public class GuerrillaMail {
	
	//For requests
	private HttpClient httpclient = new DefaultHttpClient();
    private HttpResponse httpResponse;
    private HttpGet httpGet;
    private HttpPost httpPost;
    private String stringResponse;
    private JSONObject jSonObject;

    
    //API Address
    private final String apiAddr = "http://api.guerrillamail.com/ajax.php?f=%s";
    
    //GuerrillaMail final
    public static final String EN = "en";
    public static final String FR = "fr";
    public static final String NL = "nl";
    public static final String RU = "ru";
    public static final String TR = "tr";
    public static final String UK = "uk";
    public static final String AR = "ar";
    public static final String KO = "ko";
    public static final String JP = "jp";
    public static final String ZH = "zh";
    public static final String ZH_HANT = "zh-hant";
    public static final String DE = "de";
    public static final String ES = "es";
    public static final String IT = "it";
    public static final String PT = "pt";
    
    
    //GuerrillaMail attributes
    private ArrayList<EMail> emails = new ArrayList<EMail>();
    private String lang = "en";
    private String emailAddress;
    private String sidToken;
    private long timestamp;
    private String alias;
    private int seqOldestEMail = 0;

	/**
     * Constructor.
     */
	public GuerrillaMail() throws Exception{
        _getEmailAddress();
	}
	
	
	/**
	 * Constructor.
	 * @param lang the language code. GuerrillaMail contains final Strings for this.
	 */
	public GuerrillaMail(String lang) throws Exception{
		this.lang = lang;
        _getEmailAddress();
	}

	
	/**
	 * The function is used to initialize a session and set the client with an email address.
	 * @throws Exception 
	 */
	private void _getEmailAddress() throws Exception {
		httpPost = new HttpPost(String.format(apiAddr, "get_email_address"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("lang", lang));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        jSonObject = new JSONObject(stringResponse);
        
        if(jSonObject.has("email_addr")){
        	saveData(jSonObject);
        }
        else{
        	throw new Exception("Email not found: "+stringResponse);
        }
	}
	
	
	/**
	 * Set the email address to a different email address.
	 */
	private void _setEmailUser(String emailUser) throws Exception{
		httpPost = new HttpPost(String.format(apiAddr, "set_email_user"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("email_user", emailUser));
        formparams.add(new BasicNameValuePair("lang", lang));
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        jSonObject = new JSONObject(stringResponse);
        
        if(jSonObject.has("email_addr")){
        	saveData(jSonObject);
        }
        else{
        	throw new Exception("Email not found: "+stringResponse);
        }
	}
	
	
	/**
	 * Check for new email on the server.
	 * @return list of new messages
	 */
	private ArrayList<EMail> _checkEmail() throws Exception {
		httpPost = new HttpPost(String.format(apiAddr, "check_email"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        formparams.add(new BasicNameValuePair("seq", String.valueOf(seqOldestEMail)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        jSonObject = new JSONObject(stringResponse);
        
        if(jSonObject.has("list")){
        	JSONArray jSonArray = jSonObject.getJSONArray("list");
        	
        	
        	for(int i = 0; i < jSonArray.length(); i++){
        		if(!emails.contains(jSonArray.getJSONObject(i))){
        			emails.add(new EMail(jSonArray.getJSONObject(i)));
        		}
        	}
        	
        	seqOldestEMail = emails.get(emails.size()-1).getId();
        }
        else{
        	throw new Exception("_checkEmail doesn't find list of emails: "+stringResponse);
        }
		
		return emails;
	}

	/**
	 * Get email list.
	 * @return list of new messages
	 */
	private ArrayList<EMail> _getEmailList() throws Exception{
		httpPost = new HttpPost(String.format(apiAddr, "get_email_list"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("lang", "en"));
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        formparams.add(new BasicNameValuePair("offset", String.valueOf(seqOldestEMail)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        //System.out.println("RESPONSE: "+stringResponse);
        
        jSonObject = new JSONObject(stringResponse);
        
        if(jSonObject.has("list")){
        	JSONArray jSonArray = jSonObject.getJSONArray("list");
        	
        	for(int i = 0; i < jSonArray.length(); i++){
        		if(!emails.contains(jSonArray.getJSONObject(i))){
        			emails.add(new EMail(jSonArray.getJSONObject(i)));
        		}
        	}
        	
        	//seqOldestEMail = emails.get(emails.size()-1).getMailId();
        }
        else{
        	throw new Exception("_getEmailList doesn't find list of emails: "+stringResponse);
        }
		
		return emails;
		
	}
	
	
	/**
	 * Get the contents of an email.
	 * @param emailId the id of the email you want to fetch.
	 * @return the email.
	 */
	private EMail _fetchEmail(int emailId) throws Exception {
		EMail email = null;
		if(emailExists(emailId)){
			email = emails.get(emailPosition(emailId));
			
			httpPost = new HttpPost(String.format(apiAddr, "fetch_email"));
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	        formparams.add(new BasicNameValuePair("sid_token", sidToken));
	        formparams.add(new BasicNameValuePair("email_id", String.valueOf(emailId)));
	        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
	        httpPost.setEntity(entity);
			httpResponse = httpclient.execute(httpPost);
	        stringResponse = EntityUtils.toString(httpResponse.getEntity());
	        
	        jSonObject = new JSONObject(stringResponse);
	        
	        if(jSonObject.has("mail_id")){
	        	email.setBody(jSonObject.getString("mail_body"));
	        }
	        else{
	        	throw new Exception("_fetchEmail doesn't find email content: "+stringResponse);
	        }
		}
		
		return email;
	}
	
	
	
	/**
	 * Save data of the email address from the request.
	 * @param jSonObject jsonObject
	 * @throws JSONException
	 */
	private void saveData(JSONObject jSonObject) throws JSONException{
		emailAddress = jSonObject.getString("email_addr");
    	sidToken = jSonObject.getString("sid_token");
    	timestamp = jSonObject.getLong("email_timestamp");
    	alias = jSonObject.getString("alias");
	}
	
	
	/**
	 * Check if an email is in the emails store.
	 * @param emailId the id of the email
	 * @return true or false
	 */
	private boolean emailExists(int emailId){
		for(int i = 0; i < emails.size(); i++){
    		EMail emailTemp = emails.get(i);
    		if(emailTemp.getId() == emailId){
    			return true;
    		}
    	}
		return false;
	}
	
	
	/**
	 * Return the position where the email is in the emails store.
	 * @param emailId the id of the email
	 * @return the position in the ArrayList<EMail>
	 * @throws Exception 
	 */
	private int emailPosition(int emailId) throws Exception{
		for(int i = 0; i < emails.size(); i++){
    		EMail emailTemp = emails.get(i);
    		if(emailTemp.getId() == emailId){
    			return i;
    		}
    	}
		throw new Exception("The email store doesn't contain the email.");
	}
	
	
	/**
	 * Check for new email on the server.
	 * @return list of new messages
	 */
	public ArrayList<EMail> checkEmail() throws Exception{
		return _checkEmail();
	}
	
	/**
	 * Get the contents of an email.
	 * @param emailId the id of the email you want to fetch.
	 * @return the email.
	 * @throws Exception 
	 */
	public EMail fetchEmail(int emailId) throws Exception{
		return _fetchEmail(emailId);
	}
	
	
	/* GET METHODS */
	
	
	/**
	 * Get email address.
	 * @return the email address.
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}
	
	/**
	 * Get the email list.
	 * @return list of new messages
	 */
	public ArrayList<EMail> getEmailList() throws Exception{
		return this._getEmailList();
	}
	
	
	
	
	
	/* SET METHODS */
	
	/**
	 * Set the email user part of the address.
	 * @param emailUser the email user part of the address.
	 */
	public void setEmailUser(String emailUser) throws Exception{
		_setEmailUser(emailUser);
	}
	
}