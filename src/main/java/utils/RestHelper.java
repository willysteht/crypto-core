package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

public class RestHelper {
	private HttpClient httpClient;
	private Date date;
	
	private String coreName;
	private String apiKey;
	private String apiSecretKey;
	

	public RestHelper(String coreName, String apiKey, String apiSecretKey){
		date = new Date();
		this.coreName = coreName;
		this.apiKey = apiKey;
		this.apiSecretKey = apiSecretKey;
		httpClient = HttpClientBuilder.create().build();
	}
	/**
	 * Returns a HttpResponse using the HTTP-GET Method
	 * Calls an API-Page with no further parameters
	 * @param apiString API-Page
	 * @return HttpResponse of called page
	 */
	public HttpResponse getGETResponse(String apiString){
		return getHttpResponse(new HttpGet(getRequestString(apiString)));
	}
	/**
	 * Returns a HttpResponse using the HTTP-POST Method
	 * Calls an API-Page with no further parameters
	 * @param apiString API-Page
	 * @param params the post parameters
	 * @return HttpResponse of called page
	 */
	public HttpResponse getPOSTResponse(String apiString, ArrayList<NameValuePair> params){
		HttpPost httpPost = new HttpPost(coreName);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getHttpResponse(httpPost);
	}
	/**
	 * Returns a HttpResponse using the HTTP-PUT Method
	 * Calls an API-Page with no further parameters
	 * @param apiString API-Page
	 * @return HttpResponse of called page
	 */
	public HttpResponse getPUTResponse(String apiString){
		return getHttpResponse(new HttpPut(getRequestString(apiString)));
	}
	/**
	 * Returns a HttpResponse using the HTTP-DELETE Method
	 * Calls an API-Page with no further parameters
	 * @param apiString API-Page
	 * @return HttpResponse of called page
	 */
	public HttpResponse getDELETEResponse(String apiString){
		return getHttpResponse(new HttpDelete(getRequestString(apiString)));
	}
	/**
	 * Converts a HttpResponse to a String
	 * @param response Given HttpResponse
	 * @return Response as String
	 */
	public String httpResponseToString(HttpResponse response){
		BufferedReader rd;
		StringBuilder result = new StringBuilder();
		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));			
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(result);
		return result.toString();
	}
	
	/**
	 * 
	 * @param parameters
	 * @return Returns a String with the key-value pairs formatted for URL
	 */
	public String generateUrlParameters(HashMap<String, String> parameters) {
		String urlAttachment = "?";
		Object[] keys = parameters.keySet().toArray();
		for(Object key : keys)
			urlAttachment += key.toString() + "=" + parameters.get(key) + "&";

		return urlAttachment;
	}
	
	/**
	 * Returns a HttpResponse using a HTTP-Method specified in base
	 * Calls an API-Page
	 * @param base HTTP-Method as object
	 * @return HttpResponse of called page
	 */
	private HttpResponse getHttpResponse(HttpRequestBase base){
		try {
			String encoding = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecretKey).getBytes());
			base.setHeader("Authorization", "Basic " + encoding);
			HttpResponse response = httpClient.execute(base);
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getRequestString(String apiString){
		return coreName + apiString + "?nonce="+date.getTime();
	}
}
