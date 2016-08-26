package restClient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

public class Tests {
	
	SoftAssert softAssert = new SoftAssert();
	
	@Test
	public void testGetRequest() throws Exception{
		//execute the request like using it from browser
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getRequest("http://httpbin.org/get")), 200, "Verify if response code is 200.");
		
		//execute the request with defect URL
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getRequest("http://httpbin.org/getty")), 404, "Verify if response code is 404.");
		
		//execute without /get path
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getRequest("http://httpbin.org/")), 200, "Verify if response code is 200.");
		
		//verify if host from response JSON is httpbin
		softAssert.assertEquals(RESTClient.parseJson("headers", "Host", RESTClient.getRequest("http://httpbin.org/get")).contains("httpbin"), true, "Verify the presence in JSON of httpbin.");
		
		//test unauthorized methods
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/get", "post").contains("405"), true);
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/", "get").contains("HTTP Client Testing Service"), true);
		softAssert.assertAll();
	}
	
	@Test
	public void testPostRequest() throws Exception{
		
		//execute a post request with no body content
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.postRequest("http://httpbin.org/post", "text", " ")), 200);
		
		//execute a post with test data
		String textToBeSent = "text for test";
		softAssert.assertEquals(RESTClient.parsePostJson("data", RESTClient.postRequest("http://httpbin.org/post", "text", textToBeSent)), textToBeSent);
		
		//execute a post with json data
		String jsonToBeSent = "{\"qty\":100,\"name\":\"iPad 4\"}";
		softAssert.assertEquals(RESTClient.parsePostJson("data", RESTClient.postRequest("http://httpbin.org/post", "json", jsonToBeSent)), jsonToBeSent);
		
		//execute a post with xml data
		String xmlToBeSent = "<data><employee><name>A</name>"
		        + "<title>Manager</title></employee></data>";
		softAssert.assertEquals(RESTClient.parsePostJson("data", RESTClient.postRequest("http://httpbin.org/post", "xml", xmlToBeSent)), xmlToBeSent);
		
		//execute post without /post
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.postRequest("http://httpbin.org/", "json", "{\"qty\":100,\"name\":\"iPad 4\"}")), 405);
		
		//execute post with a local drive file
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.postRequest("http://httpbin.org/post", "d:/downloaded_file")), 200);
		
		//verify json keys
		String postData = "text for test";
		softAssert.assertEquals(RESTClient.parsePostJson("data", RESTClient.postRequest("http://httpbin.org/post", "text", postData)), "text for test");
		
		//verify unauthorized methods
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/post", "get").contains("405"), true);
		
		softAssert.assertAll();
		
		
	}
	
	@Test
	public void testGetBytesRequest() throws ClientProtocolException, IOException, Exception{
		//execute a get bytes request
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getBytesData("http://httpbin.org/bytes/67")), 200);
		
		//verify bytes request with malformed URL
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getBytesData("http://httpbin.org/byte/67")), 404);
		
		//verify unauthorized methods
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/bytes/67", "post").contains("405"), true);
		
		//verify the size of generated and downloaded file
		softAssert.assertEquals(RESTClient.getFileFromBytesRequest(RESTClient.getBytesData("http://httpbin.org/bytes/67")), 67);
		
		//verify unauthorized methods
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/bytes/67", "post").contains("405"), true);
		
		//verify /bytes request using a string instead of int,
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getBytesData("http://httpbin.org/bytes/wrongType")), 404);
		
		softAssert.assertAll();
	}
	
	@Test
	public void testAuthRequest() throws Exception{
		//execute an authorization request
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getAuthorizationRequest("http://httpbin.org/basic-auth/adi/vraciu", "adrian", "loginpass")), 401);
		
		//verify auth request with malformed URL
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getAuthorizationRequest("http://httpbin.org/basic-authorization/adi/vraciu", "adrian", "loginpass")), 404);
		
		//verify auth without user and passwd
		softAssert.assertEquals(RESTClient.getStatusCode(RESTClient.getAuthorizationRequest("http://httpbin.org/basic-auth", "adrian", "loginpass")), 404);
		
		//verify unauthorized methods
		softAssert.assertEquals(RESTClient.testUnauthorizedMethods("http://httpbin.org/basic-auth/adi/vraciu", "post").contains("405"), true);
		
		softAssert.assertAll();
	}
	
}
