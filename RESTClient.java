package restClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class RESTClient {
	
	/**
	 * Returns the HTTP error code line from received html when using unauthorized methods for requests
	 * @param url of the endpoint
	 * @param requestType POST/GET/PUT/DELETE etc
	 * @return the line as string of the HTMl file which contains the error code
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String testUnauthorizedMethods(String url, String requestType) throws ClientProtocolException, IOException{
		org.jsoup.nodes.Document htmlResponse = null;
		Elements title;
		
		switch (requestType) {
		case "post":
			htmlResponse = Jsoup.connect(url).ignoreHttpErrors(true).post();
			break;
		case "get":
			htmlResponse = Jsoup.connect(url).ignoreHttpErrors(true).ignoreContentType(true).get();
		default:
			break;
		}
		
		title = htmlResponse.select("title");
		
		System.out.println(title);
		return title.text();
	}

	/**
	 * Returns the HTTP status code of the request
	 * @param response which needs to be analyzed for status
	 * @return status code as integer
	 * @throws Exception
	 */
	public static int getStatusCode(HttpResponse response) throws Exception{
		int code = 0;
		try {
			code = response.getStatusLine().getStatusCode();
			System.out.println("The returned HTTP code is: " + code);
		} catch (Exception e) {
			System.out.println("No status code found.");
			throw(e);
		}
		return code;
	}
	
	/**
	 * Parses the JSON file from GET request response
	 * @param object from JSON
	 * @param key the value of the key which needs to be extracted
	 * @param response - the HTTP response which contains the JSON file
	 * @return the key's value
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String parseJson(String object, String key, HttpResponse response) throws ClientProtocolException, IOException, JSONException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String output = readAll(br);
		
		JSONObject obj = new JSONObject(output);
		JSONObject obj2 = obj.getJSONObject(object);
		String name = obj2.getString(key);
		System.out.println(name);
		return name;
	}
	
	/**
	 * 
	 * @param jsonField
	 * @param response
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String parsePostJson(String jsonField, HttpResponse response) throws ClientProtocolException, IOException, JSONException{		
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String output = readAll(br);
		
		JSONObject obj = new JSONObject(output);
		String data = obj.getString(jsonField);
		System.out.println("The value of key is: " + data);
		return data;
	}
	
	/**
	 * Returns the size of the downloaded file, following the /bytes request
	 * @param response the HTTP response of /bytes request
	 * @return the size of file as long
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public static long getFileFromBytesRequest(HttpResponse response) throws UnsupportedOperationException, IOException{
		HttpEntity is = response.getEntity();
		InputStream content = is.getContent();
		byte[] buffer = new byte[8 * 1024];
		
		try {
			  OutputStream output = new FileOutputStream("d:\\downloaded_file");
			  try {
			    int bytesRead;
			    while ((bytesRead = content.read(buffer)) != -1) {
			      output.write(buffer, 0, bytesRead);
			    }
			  } finally {
			    output.close();
			  }
			} finally {
			  content.close();
			}
		File downloadedFile = new File("d:\\downloaded_file");
		System.out.println("Size of file is: " + downloadedFile.length());
		return downloadedFile.length();
		
	}
	
	/**
	 * Returns the HTTP response of POST request 
	 * @param url of the endpoint
	 * @param contentType text, json or xml
	 * @param postData the data to be posted as string formatted as contentType
	 * @return the response of request
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse postRequest(String url, String contentType, String... postData) throws ClientProtocolException, IOException{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);
		switch (contentType) {
		case "text":
			contentType = "application/text";
			break;
		case "json":
			contentType = "application/json";
			break;
		case "xml":
			contentType = "application/xml";
			break;
		default:
			break;
		}
		StringEntity entity = new StringEntity(postData[0]);
		entity.setContentType(contentType);
		httpPost.setEntity(entity);
		HttpResponse responsePost = httpClient.execute(httpPost);
		return responsePost;
	}
	
	/**
	 * Returns the HTTP response of the POST request when sending binary data(files)
	 * @param url of the endpoint
	 * @param filePath the path of the file which is uploaded
	 * @return the response of the request
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse postRequest(String url, String filePath) throws ClientProtocolException, IOException{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);
		
		File fileToBeSent = new File(filePath);
		FileEntity fileEntity = new FileEntity(fileToBeSent);
		httpPost.setEntity(fileEntity);
		fileEntity.setContentType("binary/octet-stream");
		HttpResponse response = httpClient.execute(httpPost);
		return response;
	}
	
	/**
	 * Returns the HTTP response of the /bytes request
	 * @param url of the bytes endpoint
	 * @return the HTTP response of the request
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse getBytesData(String url) throws ClientProtocolException, IOException{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse responseGet = httpClient.execute(httpGet);
		return responseGet;
	}
	
	/**
	 * Returns the HTTP response of the /GET request
	 * @param url of the endpoint
	 * @return a HTTP response
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse getRequest(String url) throws ClientProtocolException, IOException{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse responseGet = httpClient.execute(httpGet);
		return responseGet;
	}
	
	/**
	 * Returns the HTTP response of the get authorization request
	 * @param url of the endpoint
	 * @param username to be sent
	 * @param password to be sent
	 * @return HTTP response
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse getAuthorizationRequest(String url, String username, String password) throws ClientProtocolException, IOException{
		CredentialsProvider credentials = new BasicCredentialsProvider();
		credentials.setCredentials(new AuthScope("httpbin.org", 80), new UsernamePasswordCredentials(username, password));
		HttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentials).build();
		HttpGet getAuth = new HttpGet(url);
		HttpResponse response = httpclient.execute(getAuth);
		return response;
	}
	
	/**
	 * Method for creating a string from BufferReader object
	 * @param rd BufferReader object
	 * @return the content of BufferReader as string
	 * @throws IOException
	 */
	 private static String readAll(Reader rd) throws IOException {
		   StringBuilder sb = new StringBuilder();
		   int cp;
		   while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

}
