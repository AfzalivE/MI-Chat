package com.macinsiders.chattools;
//package com.ibm.cas.roh.wcservices;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * A class containing a useful method for contacting a web resource and receiving a response.
 * 
 * @author Dillon Dixon
 *
 */
public class HTTPExecutor {

	public static final String RespHeader = "RespHeader";
	public static final String RespBody = "RespBody";
	public static final String RespState = "RespState";
	public static final String RespStatus = "RespStatus";
	public static final String GET_INTERFACE = "GET_INTERFACE";
	public static final String POST_INTERFACE = "POST_INTERFACE";
	
	private static boolean initialized = false;
	
	private static HttpClient client;
	
	private static void initialize(){
		
		if(initialized)
			return;
		
		// Temporary solution for getting around broken/expired SSL certificates.
		//HttpClient client = getNewHttpClient();//new DefaultHttpClient();
		
		client = new DefaultHttpClient();
		
		HttpParams params = new BasicHttpParams();
		
		HttpClientParams.setRedirecting(params, true);
		
	}
	
	/**
	 * Calls an HTTP resource and gets a returns a hash map response.
	 * @param link A hash map of name value pairs (<String, String>). 'url' is reserved for the target
	 * URL and 'type' for the call interface type (POST, GET .etc).
	 * @param cookies A saved list of cookies representing the state of a previous HTTP call (not required).
	 * @return A hashmap <String, Object> containing status code (int), headers (Header[]), body (String), 
	 * cookies (Cookie[]).
	 */
	public static Map<String, Object> execute(Map<String, String> link, List<Header> headers, HttpEntity entity, HttpContext state) throws Exception {
		
		initialize();
		
		if(state == null){
			
			CookieStore cookieStore = new BasicCookieStore();
			state = new BasicHttpContext();
			
			state.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}
		
		String url = link.get("url");
			
		if(url == null)
			throw new Exception("Missing 'url' parameter");
		
		url += "?";
			
		for(String key : link.keySet())
			if(!(key.equals("type") || key.equals("url")))
				url += key + "=" + URLEncoder.encode(link.get(key), "UTF-8") + "&";
		
		url = url.substring(0, url.length() - 1);
		
		//System.out.println("Executing link @ resource [" 
		//		+ link.get("url").substring(link.get("url").lastIndexOf("/") + 1, link.get("url").length()) + "] : " + url + " ...");
		
		HttpRequestBase method = null;
		
		if(link.get("type") == null)
			throw new Exception("Missing REST interface type");
		
		// Set the type of method call.
		if(link.get("type").equals(GET_INTERFACE))
			method = new HttpGet(url);
		else if(link.get("type").equals(POST_INTERFACE))
			method = new HttpPost(url);
		else
			throw new Exception("Unknown REST interface type '" + link.get("type") + "'");
		
		if(headers != null)
			for(Header h : headers)
				method.addHeader(h);
		
		if(entity != null){
			if(method instanceof HttpPost)
				((HttpPost)method).setEntity(entity);
			else
				throw new Exception("Attempted to insert entity into 'GET' request.");
		}
		
		HttpResponse responseData = client.execute(method, state);
		
		int responseCode = responseData.getStatusLine().getStatusCode();
		
		// Always expecting 200 response codes for success from non-REST resource.
		if (responseCode != HttpStatus.SC_OK){
		  System.out.println("Response code of " + responseCode + " received!");
		  throw new Exception("Bad response of ..." + responseCode);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(responseData.getEntity().getContent()));
		
		String line;
		StringBuffer sb = new StringBuffer();
		
		while ((line = br.readLine()) != null)
			sb.append(line);
		
		br.close();	

		// Removing all tabs/newlines from the response.
		String response = sb.toString();
				
		HashMap<String, Object> result = new HashMap<String, Object>();
	    
		// Filter out cookies requesting to be deleted.
		
	    result.put(RespHeader, responseData.getAllHeaders());
		result.put(RespBody, response);
		result.put(RespState,  state);
		result.put(RespStatus, responseCode);
		
		return result;
	}
	
	// Gets an HTTP client that does not care about any of the complicated necessities of HTTPS for testing.
	public static HttpClient getNewHttpClient() {
	    try {
	    	
	        SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
	            @Override
	            public boolean isTrusted(X509Certificate[] chain,
	                    String authType) throws CertificateException {
	                return true;
	            }
	        }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        SchemeRegistry registry = new SchemeRegistry();
	        
	        registry.register(new Scheme("http", 80,  PlainSocketFactory.getSocketFactory()));
	        registry.register(new Scheme("https", 443, sf));
	        
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(registry);
	        
	        return new DefaultHttpClient(ccm);
	        
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
}