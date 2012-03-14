package ktmt.k52.viettts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Lớp này sử dụng để gửi request đến server và nhận phản hồi từ server ngược
 * trở lại
 * <p>
 * Quy trình sẽ là : Gửi yêu cầu chứa dữ liệu là văn bản tiêng việt ->server Và
 * nhận phản hồi từ server trở lại chứa link file âm thanh.
 * 
 * @author DungNT
 */

public class HttpHelp {

	/**
	 * Sử dụng để thực hiện các lệnh request đến server
	 * 
	 * @see DefaultHttpClient
	 */
	DefaultHttpClient httpClient;

	/**
	 * Bối cảnh để {@link HttpClient} gửi yêu cầu
	 * 
	 * @see HttpContext
	 */

	HttpContext localContext;

	/**
	 * Biến trả về có dừng request đến server hay không
	 */
	private boolean abort;
	/**
	 * Dữ liệu dạng {@link String} từ server trả về sau khi request
	 */
	private String ret;

	/**
	 * Dữ liệu dạng {@link HttpResponse} từ server trả về sau khi request
	 * 
	 * @see HttpResponse
	 */
	HttpResponse response = null;

	/**
	 * HTTP POST method.
	 * 
	 * @see HttpPost
	 */
	HttpPost httpPost = null;

	/**
	 * Contructor
	 * <p>
	 * Khởi tạo biến {@link HttpClient} và {@link HttpContext}
	 * 
	 * @see HttpClient
	 * @see HttpContext
	 */
	public HttpHelp() {
		httpClient = new DefaultHttpClient();
		localContext = new BasicHttpContext();
	}

	/**
	 * Xóa cookie.
	 */
	public void clearCookies() {

		httpClient.getCookieStore().clear();

	}

	/**
	 * Dừng yêu cầu
	 * 
	 */
	public void abort() {

		try {
			if (httpClient != null) {
				System.out.println("Abort.");
				httpPost.abort();
				abort = true;
			}
		} catch (Exception e) {
			System.out.println("HTTPHelp : Abort Exception : " + e);
		}
	}

	/**
	 * Gửi yêu cầu đến server
	 * 
	 * @param url
	 *            địa chỉ server
	 * @param data
	 *            dữ liệu request đến server
	 * @return dữ liệu từ server gửi về sau khi request có dạng {@link String}
	 * @throws ParseException
	 *             Khi địa chỉ server không hợp lệ
	 * @throws IOException
	 *             khi có lỗi xảy ra trong khi dữ liệu vào hoặc ra
	 * @see ParseException
	 * @see IOException
	 */
	public String postPage(String url, String data) throws ParseException,
			IOException {

		ret = null;

		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.RFC_2109);

		httpPost = new HttpPost(url);
		response = null;

		StringEntity tmp = null;

		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (X11; U; Linux "
						+ "i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
		httpPost.setHeader(
				"Accept",
				"text/html,application/xml,"
						+ "application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			tmp = new StringEntity(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("HTTPHelp : UnsupportedEncodingException : " + e);
		}

		httpPost.setEntity(tmp);

		try {
			response = httpClient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			System.out.println("HTTPHelp : ClientProtocolException : " + e);
		} catch (IOException e) {
			System.out.println("HTTPHelp : IOException : " + e);
		}
		ret = response.getStatusLine().toString();
		HttpEntity entity = response.getEntity();
		String responseText = EntityUtils.toString(entity);

		return responseText;
	}

	/**
	 * Gửi yêu cầu đến server mặc định là "http://isolar.vn/social/demo.php"
	 * 
	 * @param data
	 *            dữ liệu request đến server,dạng tiếng việt có dấu
	 * @return dữ liệu từ server gửi về sau khi request có dạng {@link String}
	 * @throws ParseException
	 *             Khi địa chỉ server không hợp lệ
	 * @throws IOException
	 *             khi có lỗi xảy ra trong khi dữ liệu vào hoặc ra
	 * @see ParseException
	 * @see IOException
	 */
	public String postPageIsolar(String data) throws ParseException,
			IOException {

		
		data = "voice=male1&SSinput=" + data + "&formSubmit=Submit";

		String url = "http://isolar.myftp.org/social/demo.php";
		String url1="http://isolar.vn/";
		ret = null;

		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.RFC_2109);

		httpPost = new HttpPost(url);
		response = null;

		StringEntity tmp = null;

		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (X11; U; Linux "
						+ "i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
		httpPost.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

		httpPost.setHeader("Host", "isolar.myftp.org");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		try {
			tmp = new StringEntity(data, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			System.out
					.println("HTTPHelp : UnsupportedEncodingException : " + e);
		}
		httpPost.setEntity(tmp);
		try {
			response = httpClient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			System.out.println("HTTPHelp : ClientProtocolException : " + e);
		} catch (IOException e) {
			System.out.println("HTTPHelp : IOException : " + e);
		}
		ret = response.getStatusLine().toString();
		HttpEntity entity = response.getEntity();
		String responseText = EntityUtils.toString(entity);

		return responseText;
	}

	/**
	 * Từ thông điệp phản hồi từ server,ta trích lấy link file audio tiếng việt
	 * 
	 * @param response
	 *            dữ liệu phản hồi từ server
	 * @return link file audio
	 */
	public String getIsolarAudioUrl(String response) {
		try {
			// xu ly reponse string
			int test = response.indexOf("href");
			int test2 = response.lastIndexOf("title");
			String test3 = response.substring(test + 6, test2 - 2);

			String urlAudio = "http://isolar.vn/social/" + test3;
			return urlAudio;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	/**
	 * Gửi yêu cầu đến server mặc định là "http://vozme.com"
	 * 
	 * @param data
	 *            dữ liệu request đến server,dạng tiếng Anh
	 * @return dữ liệu từ server gửi về sau khi request có dạng {@link String}
	 * @throws ParseException
	 *             Khi địa chỉ server không hợp lệ
	 * @throws IOException
	 *             khi có lỗi xảy ra trong khi dữ liệu vào hoặc ra
	 * @see ParseException
	 * @see IOException
	 */
	public String postPageVozMe(String data) throws ParseException, IOException {
		// giong het phuong thuc tren nhung danh rieng cho trang
		// http://vozme.com
		// url = host+POST

		String newdata = "interface=full&gn=ml&text="
				+ data.replaceAll(" ", "+");
		newdata = newdata.replaceAll("\000", "");

		String url = "http://vozme.com/text2voice.php?lang=en";
		ret = null;

		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.RFC_2109);

		httpPost = new HttpPost(url);
		response = null;

		StringEntity tmp = null;

		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (X11; U; Linux "
						+ "i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
		httpPost.setHeader(
				"Accept",
				"text/html,application/xml,"
						+ "application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader("Referer", "http://vozme.com/index.php?lang=en");

		httpPost.setHeader("Host", "vozme.com");

		try {
			// tmp = new StringEntity(data,"UTF-8");

			tmp = new StringEntity(newdata);

		} catch (UnsupportedEncodingException e) {
			System.out
					.println("HTTPHelp : UnsupportedEncodingException : " + e);
		}

		httpPost.setEntity(tmp);

		try {

			response = httpClient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			System.out.println("HTTPHelp : ClientProtocolException : " + e);
		} catch (IOException e) {
			System.out.println("HTTPHelp : IOException : " + e);
		} catch (Exception e) {
			System.out.println("HTTPHelp : Exception : " + e.getMessage());
		}
		ret = response.getStatusLine().toString();
		HttpEntity entity = response.getEntity();
		String responseText = EntityUtils.toString(entity);

		return responseText;
	}

	/**
	 * Từ thông điệp phản hồi từ server,ta trích lấy link file audio tiếng Anh
	 * 
	 * @param response
	 *            dữ liệu phản hồi từ server
	 * @return link file audio
	 */
	public String getVozMeAudioUrl(String response) throws Exception {

		// xu ly reponse string
		int test = response.lastIndexOf("href");
		int test2 = response.lastIndexOf("onclick");
		String test3 = response.substring(test + 6, test2 - 2);

		return test3.replace("\"", "");

	}

}
