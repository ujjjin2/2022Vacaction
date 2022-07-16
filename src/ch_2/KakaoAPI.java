package ch_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class KakaoAPI {

	static String url;
	static RestTemplate restTemplate = new RestTemplate();;
	static HttpHeaders header = new HttpHeaders();

	// 토큰 발급
	public void getAccessToken(String authorize_code, String client_id) throws JSONException {
		System.out.println("getAccessToken 메서드");
		String reqURL = "https://kauth.kakao.com/oauth/token";
		String access_Token = "";
		String refresh_Token = "";

		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// POST 요청을 위해 기본값이 false인 setDoOutput을 true로 변경
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			sb.append("&client_id=" + client_id); // rest_api 앱 키 입력
			sb.append("&redirect_uri=https://localhost.com");
			sb.append("&code=" + authorize_code); // 발급받은 code 입력
			bw.write(sb.toString());
			bw.flush();

			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";
			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			// Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);
			access_Token = element.getAsJsonObject().get("access_token").getAsString();
			refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
			System.out.println("access_token : " + access_Token);
			System.out.println("refresh_token : " + refresh_Token);

			br.close();
			bw.close();

			// 나에게 카카오톡 메시지 보내는 메서드
			sendMessageToMe(access_Token);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 토큰 저장하는 메서드 (사용 X)
	public void save_tokens(String tokens) throws IOException {
		System.out.println("save_tokens 메서드");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		// Json key, value 추가
		JsonObject kakao_token = new JsonObject();
		kakao_token.addProperty("filename", "kakao_token");
		kakao_token.addProperty("tokens", tokens);

		// JsonObject를 파일에 쓰기
		FileWriter fw = new FileWriter("c:\\temp\\kakaoAPI_Tokens.json");
		gson.toJson(kakao_token, fw); // JsonObject -> JSON 문자열로 변경
		fw.flush();
		fw.close();

	}

	// 토큰 읽어오는 메서드 (사용 X)
	public JsonObject load_tokens(String filename) throws IOException {
		System.out.println("load_tokens 메서드");
		// FileReader 생성
		Reader reader = new FileReader("c:\\temp\\kakaoAPI_Tokens.json");

		// Json 파일 읽어서, Lecture 객체로 변환
		Gson gson = new Gson();
		JsonObject obj = gson.fromJson(reader, JsonObject.class);

		return obj;
	}

	// refresh토큰으로 access 토큰 갱신하는 메서드 (사용 X)
	public void update_tokens(String refresh_Token, String client_id) throws IOException, JSONException {
		System.out.println("update_tokens 메서드");
		String reqURL = "https://kauth.kakao.com/oauth/token";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// POST 요청을 위해 기본값이 false인 setDoOutput을 true로 변경
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=refresh_token");
			sb.append("&client_id="+client_id); // rest_api 앱 키 입력
			sb.append("&refresh_token=" + refresh_Token); // 발급받은 code 입력
			bw.write(sb.toString());
			bw.flush();

			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 클라이언트 정보 확인하는 메서드(사용 X)
	public HashMap<String, Object> getUserInfo(String access_Token) throws JSONException {
		System.out.println("getUserInfo 메서드");

		// 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
		HashMap<String, Object> userInfo = new HashMap<>();
		String reqURL = "https://kapi.kakao.com/v2/user/me";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");

			// 요청에 필요한 Header에 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_Token);

			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);

			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
			JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

			String nickname = properties.getAsJsonObject().get("nickname").getAsString();
			String email = kakao_account.getAsJsonObject().get("email").getAsString();

			userInfo.put("nickname", nickname);
			userInfo.put("email", email);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userInfo;
	}

	// 카카오 로그아웃 (사용 X)
	public void kakaoLogout(String access_Token) {
		String reqURL = "https://kapi.kakao.com/v1/user/logout";
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + access_Token);

			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String result = "";
			String line = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println(result);
		} catch (IOException e) { 
			// TODO Auto-generated
			e.printStackTrace();
		}
	}

	// 나에게 카카오톡 메시지 보내는 메서드
	public String sendMessageToMe(String access_Token) throws JSONException, IOException { 
		url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
		header.add("Authorization", "Bearer " + access_Token);

		System.out.println("----- Input Tokens Finish ----");
		// data 입력
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("template_object", buildTemplateObject());

		System.out.println("---- Input Data Finish ----");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, header);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		System.out.println(response);

		return response.getStatusCode().name();
	}

	// 메시지 내용 입력하는 메서드
	private String buildTemplateObject() throws JSONException {
		System.out.println("buildTemplateObject 메시지 내용 보내는 메서드");
		JSONObject templateObject = new JSONObject();
		JSONObject urlObject = new JSONObject();
		templateObject.put("object_type", "text");
		templateObject.put("text", "Hello, world!");
		
		urlObject.put("web_url", "www.naver.com");
		
		templateObject.put("link", urlObject);
		templateObject.put("button_title", "바로 확인");
		System.out.println(templateObject.toString());
		
		return templateObject.toString();
	}

	public static void main(String[] args) throws JSONException {
		// 카카오톡 인증 코드, client_id 메서드에 입력
		KakaoAPI kakaoAPI = new KakaoAPI();
		kakaoAPI.getAccessToken("", "");
	}
}