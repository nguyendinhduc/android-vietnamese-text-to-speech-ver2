package ktmt.k52.viettts;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpHelp {

    static DefaultHttpClient httpClient = new DefaultHttpClient();
    static HttpContext localContext = new BasicHttpContext();
    private static boolean abort;
    private static String ret;

    static HttpResponse response = null;
    static HttpPost httpPost = null;

    public HttpHelp(){

    }

    public static void clearCookies() {

    	httpClient.getCookieStore().clear();

    }

    public static void abort() {

    	try {
    		if(httpClient!=null){
    			System.out.println("Abort.");
    			httpPost.abort();
    			abort = true;
    		}
    	} catch (Exception e) {
    		System.out.println("HTTPHelp : Abort Exception : "+e);
    	}
    }

    public static String postPage(String url, String data, boolean returnAddr) throws ParseException, IOException 
    {

    	ret = null;

    	httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

    	httpPost = new HttpPost(url);
    	response = null;

    	StringEntity tmp = null;		

    	httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux " +
    		"i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
    	httpPost.setHeader("Accept", "text/html,application/xml," +
    		"application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    	httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

    	try {
    		tmp = new StringEntity(data,"UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		System.out.println("HTTPHelp : UnsupportedEncodingException : "+e);
    	}

    	httpPost.setEntity(tmp);

    	try {
    		response = httpClient.execute(httpPost,localContext);
    	} catch (ClientProtocolException e) {
    		System.out.println("HTTPHelp : ClientProtocolException : "+e);
    	} catch (IOException e) {
    		System.out.println("HTTPHelp : IOException : "+e);
    	} 
                ret = response.getStatusLine().toString();
                HttpEntity entity = response.getEntity();
                String responseText = EntityUtils.toString(entity);

                return responseText;
                }
    
    public static String postPageIsolar(String data) throws ParseException, IOException
    {
    	//giong het phuong thuc tren nhung danh rieng cho trang isolar.vn
    	
    	data = "voice=male1&SSinput="+data+"&formSubmit=Submit";
		String url = "http://isolar.vn/social/demo.php";
    	ret = null;

    	httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

    	httpPost = new HttpPost(url);
    	response = null;

    	StringEntity tmp = null;		

    	httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux " +
    		"i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
    	httpPost.setHeader("Accept", "text/html,application/xml," +
    		"application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    	httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

    	try {
    		tmp = new StringEntity(data,"UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		System.out.println("HTTPHelp : UnsupportedEncodingException : "+e);
    	}

    	httpPost.setEntity(tmp);

    	try {
    		response = httpClient.execute(httpPost,localContext);
    	} catch (ClientProtocolException e) {
    		System.out.println("HTTPHelp : ClientProtocolException : "+e);
    	} catch (IOException e) {
    		System.out.println("HTTPHelp : IOException : "+e);
    	} 
                ret = response.getStatusLine().toString();
                HttpEntity entity = response.getEntity();
                String responseText = EntityUtils.toString(entity);

                return responseText;
    }
    
    public static String getIsolarAudioUrl(String response)
    {
    	try
    	{
    	// xu ly reponse string
		int test = response.indexOf("href");
		int test2 = response.lastIndexOf("title");
		String test3 = response.substring(test + 6, test2-2);

		String urlAudio = "http://isolar.vn/social/" + test3;
		return urlAudio;
    	}catch(Exception ex)
    	{
    		System.out.println(ex.getMessage());
    	}
		return null;
    }
}



