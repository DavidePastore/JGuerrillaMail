/**
 * 
 */
package com.guerrillamail.www;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
	}
	
	
	/**
	 * Constructor.
	 * @param lang the language code. GuerrillaMail contains final Strings for this.
	 */
	public GuerrillaMail(String lang) throws Exception{
		this.lang = lang;
	}

	
	/**
	 * The function is used to initialize a session and set the client with an email address.
	 * If the account is expired you will get another address.
	 * @throws Exception 
	 */
	private void _getEmailAddress() throws Exception {
		httpPost = new HttpPost(String.format(apiAddr, "get_email_address"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("lang", lang));
        
        if(sidToken != null){
        	formparams.add(new BasicNameValuePair("sid_token", sidToken));
        }
        
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
        
        System.out.println("RESPONSE: "+stringResponse);
        
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
	 * Forget the current email address.
	 * @return true if successful, false otherwise.
	 * @throws Exception 
	 */
	private void _forgetMe() throws Exception{
		httpPost = new HttpPost(String.format(apiAddr, "forget_me"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("lang", "en"));
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        formparams.add(new BasicNameValuePair("email_addr", emailAddress));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());

        if(!Boolean.valueOf(stringResponse)){
        	throw new Exception("forgetMe isn't successfull.");
        }
        else{
        	emailAddress = null;
        	emails = new ArrayList<EMail>();
        	seqOldestEMail = -1;
        	sidToken = null;
        	timestamp = -1;
        }
	}
	
	/**
	 * Delete all the email with the parameter id.
	 * @param emailIds the ArrayList with all the emailId that you want to delete.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private void _delEmail(ArrayList<Integer> emailIds) throws ClientProtocolException, IOException{
		Iterator<Integer> iterator = emailIds.iterator();
		httpPost = new HttpPost(String.format(apiAddr, "del_email"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("lang", "en"));
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        
        //Adding all the email ids in the form
        while(iterator.hasNext()){
        	formparams.add(new BasicNameValuePair("email_ids[]", Integer.toString(iterator.next())));
        }
        
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        //System.out.println("RESPONSE: "+stringResponse);
	}
	
	/**
	 * Extend the email address time by 1 hour. A maximum of 2 hours can be extended.
	 * @throws Exception
	 */
	private void _extend() throws Exception{
		httpPost = new HttpPost(String.format(apiAddr, "extend"));
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("lang", "en"));
        formparams.add(new BasicNameValuePair("sid_token", sidToken));
        
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(entity);
		httpResponse = httpclient.execute(httpPost);
        stringResponse = EntityUtils.toString(httpResponse.getEntity());
        
        jSonObject = new JSONObject(stringResponse);
        
        if(jSonObject.has("expired")){
        	
        	if(jSonObject.getBoolean("expired")){
        		throw new Exception("The email has expired.");
        	}
        	
        	timestamp = jSonObject.getLong("email_timestamp");
        	sidToken = jSonObject.getString("sid_token");
        	int affected = jSonObject.getInt("affected");
        	
        	if(!Utils.fromIntToBoolean(affected)){
        		throw new Exception("The extension is not successful.");
        	}
        	
        }
        else{
        	throw new Exception("_extend doesn't find response content: "+stringResponse);
        }
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
	
	
	/**
	 * Forget the current email address.
	 * @throws Exception 
	 */
	public void forgetMe() throws Exception{
		if(emailAddress != null){
			_forgetMe();
		}
	}
	
	
	/**
	 * Delete all the email with the parameter id.
	 * @param emailIds the ArrayList with all the emailId that you want to delete.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void delEmail(ArrayList<Integer> emailIds) throws ClientProtocolException, IOException{
		_delEmail(emailIds);
	}
	
	
	/**
	 * Extend the email address time by 1 hour. A maximum of 2 hours can be extended.
	 * @throws Exception 
	 */
	public void extend() throws Exception{
		if(emailAddress != null){
			_extend();
		} else{
			throw new Exception("Email address is null.");
		}
	}
	
	
	/* GET METHODS */
	
	
	/**
	 * Get email address. If the account is expired you will get another address.
	 * @return the email address.
	 * @throws Exception 
	 */
	public String getEmailAddress() throws Exception {
		if(emailAddress == null){
			_getEmailAddress();
		}
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
