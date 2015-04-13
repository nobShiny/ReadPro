package com.shiny.douban.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gdata.client.douban.DoubanService;
import com.shiny.douban.entity.Review;
import com.shiny.douban.entity.Subject;
import com.shiny.douban.entity.Topic;

//������ʹ�����
public class NetUtil {

	private static String loginUrl = "http://www.douban.com/accounts/login";
	private static String bookUrl = "http://api.douban.com/book/subject/";
	private static String reviewUrl = "http://www.douban.com/review/";
	private static String peopleUrl = "http://api.douban.com/people/";

	private static String apiKey = "078409f18961d372168c1dd49c257a56"; 
	private static String secret = "0dd3419e39fe4b2c"; 

	private static DoubanService doubanService = new DoubanService("���Ҷ�", apiKey, secret);

	// ��¼�û���ID
	private static String uid;
	private static String selfUri;
	private static String accessToken;
	private static String tokenSecret;
	
	//cookies
	private static  CookieStore ckStore=null;
	
	public static CookieStore getCkStore() {
		return ckStore;
	}

	public static void setCkStore(CookieStore ckStore) {
		NetUtil.ckStore = ckStore;
	}
	
	public static String getSecret() {
		return secret;
	}

	public static String getUid() {
		return uid;
	}

	public static void setUid(String uid) {
		NetUtil.uid = uid;
		NetUtil.selfUri = peopleUrl + uid;
	}

	public static String getSelfUri() {
		return selfUri;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static void setAccessToken(String accessToken) {
		NetUtil.accessToken = accessToken;
	}

	public static String getTokenSecret() {
		return tokenSecret;
	}

	public static void setTokenSecret(String tokenSecret) {
		NetUtil.tokenSecret = tokenSecret;
	}

	public static DoubanService getDoubanService() {
		return doubanService;
	}

	// ͼƬ���ع�����
	public static AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

	// ��ȡ��֤��ID
	public static String getCaptchaId() throws Exception {
		HttpGet request = new HttpGet(loginUrl);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Source source = new Source(response.getEntity().getContent());
				List<Element> inputElements = source
						.getAllElements(HTMLElementName.INPUT);
				for (Element element : inputElements) {
					String name = element.getAttributeValue("name");
					if ("captcha-id".equals(name)) {
						return element.getAttributeValue("value");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("��ȡ��֤��ʧ�ܣ�");
		}
		throw new Exception("��ȡ��֤��ʧ�ܣ�");
	}

	// ��ȡ��֤��ͼƬ
	public static Bitmap getCaptchaImg(String captchaId) throws Exception {
		String url = "http://www.douban.com/misc/captcha?id=";
		url = url + captchaId + "&amp;size=m";
		try {
			URL imageUri = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) imageUri
					.openConnection();
			httpConn.setDoInput(true);
			httpConn.connect();
			InputStream is = httpConn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("��ȡ��֤��ͼƬʧ�ܣ�");
		}
	}

	// ��¼���꣬����ȡaccessToken
	public static boolean doubanLogin(String email, String password,
			String captchaId, String captchaValue) throws Exception {
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(loginUrl);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("form_email", email));
		nameValuePairs.add(new BasicNameValuePair("form_password", password));
		nameValuePairs.add(new BasicNameValuePair("source", "simple"));
		nameValuePairs.add(new BasicNameValuePair("captcha-id", captchaId));
		nameValuePairs.add(new BasicNameValuePair("captcha-solution",captchaValue));
		nameValuePairs.add(new BasicNameValuePair("remember","true"));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpClient.execute(httpPost);
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		ckStore=httpClient.getCookieStore();
		
		// ��¼�ɹ�,��ʼ��Ȩ
		if (isLoginStatus(cookies)) {
			return getAccessToken(httpClient);
		}
		return false;
	}
	

	// �ж��Ƿ��¼�ɹ�
	public static boolean isLoginStatus(List<Cookie> cookies) {
		boolean status = false;
		if (!cookies.isEmpty()) {
			for (int i = 0; i < cookies.size(); i++) {
				Cookie cookie = cookies.get(i);
				// ��¼�ɹ�
				if ("ue".equals(cookie.getName())) {
					status = true;
					break;
				}
			}
		}
		return status;
	}

	// ȡ��AccessToken
	private static boolean getAccessToken(DefaultHttpClient httpClient)
			throws Exception {
		//�����doubanService
		String authUrl = doubanService.getAuthorizationUrl(null);
		String ck = "UMat";
		//doubanService�õ�RequestToken
		String oauth_token = doubanService.getRequestToken();
		String ssid = "2e8fa7a8";
		String oauth_callback = "";
		String confirm = "ͬ��";
		HttpPost httpPost = new HttpPost(authUrl);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("ck", ck));
		nameValuePairs.add(new BasicNameValuePair("oauth_token", oauth_token));
		nameValuePairs.add(new BasicNameValuePair("ssid", ssid));
		nameValuePairs.add(new BasicNameValuePair("oauth_callback",oauth_callback));
		nameValuePairs.add(new BasicNameValuePair("confirm", confirm));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		httpClient.execute(httpPost);
		//doubanService�õ�AccessToken
		ArrayList<String> tokens = doubanService.getAccessToken();
		if (tokens != null) {
			accessToken = tokens.get(0);
			tokenSecret = tokens.get(1);
			Log.d("accessToken", accessToken);
			Log.d("tokenSecret", tokenSecret);

			// ��ȡ�û�ID
			uid = doubanService.getAuthorizedUser().getUid();

			return true;
		}
		return false;
	}

	// ��ȡ�����ϵ�ͼƬ
	public static Bitmap getNetImage(String url) throws Exception {
		try {
			URL imageUri = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) imageUri
					.openConnection();
			httpConn.setDoInput(true);
			httpConn.connect();
			InputStream is = httpConn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("��ȡ��֤��ͼƬʧ�ܣ�");
		}
	}

	// ��ȡ��������
	public static List<Subject> getDoubanNewBooks() throws Exception {
		List<Subject> books = new ArrayList<Subject>();
		URL uri = new URL("http://book.douban.com/latest");
		HttpURLConnection httpConn = (HttpURLConnection) uri.openConnection();
		httpConn.setDoInput(true);
		httpConn.connect();
		InputStream is = httpConn.getInputStream();
		Source source = new Source(is);
		List<Element> divs = source.getAllElements("li");
		for (Element e : divs) {
			List<Element> childs = e.getChildElements();
			if (childs.size() == 2) {
				Element contents = childs.get(0);
				Element otherinfo = childs.get(1);
				String id = otherinfo.getAttributeValue("href");
				String img = otherinfo.getChildElements().get(0).getAttributeValue("src");

				if ("detail-frame".equals(childs.get(0).getAttributeValue("class"))) {

					Subject book = new Subject();

					id = id.substring(0, id.length() - 1);
					id = id.substring(id.lastIndexOf("/") + 1);
					id = bookUrl + id;
					book.setUrl(id);
					book.setImgUrl(img);
					book.setTitle(contents.getChildElements().get(0)
							.getTextExtractor().toString());
					book.setDescription(contents.getChildElements().get(1)
							.getTextExtractor().toString());
					book.setSummary(contents.getChildElements().get(2)
							.getTextExtractor().toString());

					// �����ȡ�������֣���Ϊ-1
					book.setRating(-1f);
					book.setType(Subject.BOOK);

					books.add(book);
				}

			}

		}
		is.close();
		Collections.shuffle(books);

		return books;
	}

	// ��ȡ����ȫ��
	public static Review getReviewContentAndComments(Review review)
			throws Exception {
		HttpGet request = new HttpGet(reviewUrl + review.getId() + "/");
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Source source = new Source(response.getEntity().getContent());
			Element contentDivElement = source.getElementById("content");
			for (Element e : contentDivElement.getAllElements("img")) {
				if ("pil".equals(e.getAttributeValue("class"))) {
					review.setAuthorImageUrl(e.getAttributeValue("src"));
					review.setAuthorImage(getNetImage(review
							.getAuthorImageUrl()));
					break;
				}
			}

			for (Element e : contentDivElement.getAllElements("span")) {
				if ("v:description".equals(e.getAttributeValue("property"))) {
					String content = e.getContent().toString();
					review.setContent(content);
					break;
				}
			}

			Element commentsDiv = source.getElementById("comments");
			if (commentsDiv != null) {
				String comments = commentsDiv.getContent().toString();
				review.setComments(comments);
			}
		}
		return review;
	}

	// ��ʾ����
	public static void transportReview(Review review) {
		String comments = review.getComments();
		String content = review.getContent();
		if (content != null) {
			content = content.replaceAll("&gt; ������Ӧ", "");
		}
		if (comments != null) {
			comments = comments.replaceAll("&gt; ������Ӧ", "");
			comments = comments.replaceAll("<h3>", "<h6>");
			comments = comments.replaceAll("</h3>", "</h6>");
		}
		int pageIndex = comments.indexOf("<div class=\"paginator\">");
		if (pageIndex > 0) {
			comments = comments.substring(0, pageIndex);
		}
		review.setContent(content);
		review.setComments(comments);
	}

	/**
	 * ��ȡ�������ܻ�ӭ����
	 * 
	 * @param start
	 * @return
	 */
	public static List<Review> getBestReviews(int start) {
		int max = 50;
		List<Review> reviews = new ArrayList<Review>();
		// ���50��
		if (start >= max) {
			return reviews;
		}
		try {
			URL uri = new URL("http://book.douban.com/review/best/?start="
					+ start);
			HttpURLConnection httpConn = (HttpURLConnection) uri
					.openConnection();
			httpConn.setDoInput(true);
			httpConn.connect();
			InputStream is = httpConn.getInputStream();
			Source source = new Source(is);

			Element divContent = source.getElementById("content");
			for (Element item : divContent
					.getAllElementsByClass("tlst clearfix")) {
				String reviewTitle = item.getFirstElement("a")
						.getTextExtractor().toString();
				String reviewUrl = item.getFirstElementByClass("j a_unfolder")
						.getAttributeValue("href");

				Element subjectElement = item.getFirstElementByClass("ilst")
						.getContent().getFirstElement();
				String subjectUrl = subjectElement.getAttributeValue("href");
				String subjectTitle = subjectElement.getAttributeValue("title");

				Element authorElement = item.getFirstElementByClass("starb")
						.getContent().getFirstElement();
				String authorUrl = authorElement.getAttributeValue("href");
				String authorName = authorElement.getTextExtractor().toString();
				String summary = item.getAllElements("div").get(1)
						.getTextExtractor().toString();

				float rating = 0;
				for (int i = 1; i <= 5; i++) {
					String cssClass = "stars" + i + " stars";
					if (item.getAllElementsByClass(cssClass).size() > 0) {
						rating = i;
						break;
					}
				}

				Review review = new Review();

				// ��������URL
				authorUrl = authorUrl.replaceFirst("book", "api");
				authorUrl = authorUrl.substring(0, authorUrl.length() - 1);
				review.setAuthorId(authorUrl);
				review.setAuthorName(authorName);

				// ��������URL
				reviewUrl = reviewUrl.replaceFirst("book", "api");
				reviewUrl = reviewUrl.substring(0, reviewUrl.length() - 1);
				review.setUrl(reviewUrl);

				review.setTitle(reviewTitle);
				review.setSummary(summary);
				review.setRating(rating);
				review.setSelf(false);// ȫ����Ϊ���˵����ۣ����ɱ༭

				Subject subject = new Subject();
				subjectUrl = subjectUrl.replaceFirst("book", "api");
				subjectUrl = subjectUrl.substring(0, subjectUrl.length() - 1);
				subject.setUrl(subjectUrl);
				subject.setTitle(subjectTitle);
				subject.setType(Subject.BOOK); // ����Ϊͼ��
				review.setSubject(subject);
				reviews.add(review);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reviews;

	}
	
	//��ȡ�����С��
	public List<Group> getGroup(int count)
	{
		List<Group> groupList=new ArrayList<Group>();
		String groupurl="http://www.douban.com/group/";
		try {
			HttpGet request = new HttpGet(groupurl);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setCookieStore(ckStore);
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Source source=new Source(response.getEntity().getContent());
			List<Element> dlContent=source.getAllElements("dl");
			Log.d("element", "size"+dlContent.size());
			for(Element element:dlContent)
			{
				Group group=new Group();
				int temp=element.getAllElements().size();
				Log.d("element", temp+"--the"+element.getName());
				List<Element> ele=element.getAllElements("a");
				for(Element e2:ele)
				{
					String url=e2.getAttributeValue("href");
					group.setUrl(url);Log.d("element", url);
				}
				List<Element> ele2=element.getAllElements("img");
				for(Element e2:ele2)
				{
					String imgUrl=e2.getAttributeValue("src");
					String name=e2.getAttributeValue("alt");
					group.setName(name);
					group.setImgUrl(imgUrl);
					Log.d("element", imgUrl);
					Log.d("element", name);
				}
				
				List<Element> ele3=element.getAllElements("span");
				String totalPeople=ele3.get(0).getContent().toString();
				group.setTotalPeople(totalPeople);
				
				groupList.add(group);
				Log.d("element", totalPeople);
			}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupList;
	}
	
	//С��Ļ����б�
	public List<Topic> getTopicList(String url)
	{
		List<Topic> topicList =new ArrayList<Topic>();
		HttpGet request = new HttpGet(url);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Source source=new Source(response.getEntity().getContent());
				List<Element> content=source.getAllElements("table");
				for(int i=0;i<content.size();i++)
				{
					if("olt".equals(content.get(i).getAttributeValue("class")) )
					{
						List<Element> elements=content.get(i).getAllElements("tr");
				
						for(int j=0;j<elements.size();j++)
						{
					
							String href="",author="",title="",reply,time;
							Topic topic;
							List<Element> ele=elements.get(j).getAllElements("td");
							//Log.d("element","-------------------------");
							List<Element> e1=ele.get(0).getAllElements("a");
							for(Element e:e1)
							{
								href=e.getAttributeValue("href");
								title=e.getAttributeValue("title");
							}
							List<Element> e2=ele.get(1).getAllElements("a");
							for(Element e:e2)
							{
								author=e.getContent().toString();
							}
							if(ele.size()==4)
							{
								reply=ele.get(2).getContent().toString();
								time=ele.get(3).getContent().toString();
							}
							else
							{
								reply="0";
								time=ele.get(2).getContent().toString();
							}
						//	Log.d("element", author+"---"+href+"---"+title+"---"+reply+"---"+time);
							topic=new Topic();
							topic.setAuthor(author);
							topic.setTitle(title);
							topic.setReply(reply);
							topic.setLastdate(time);
							topic.setUrl(href);
							
							topicList.add(topic);
					
						}
					}
					
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return topicList;
	}
	
	//�õ����������
	public List<Topic> getTopic(String url)
	{
		List<Topic> list=new ArrayList<Topic>();
		HttpGet httpGet=new HttpGet(url);
		DefaultHttpClient client=new DefaultHttpClient();
		
		try {
			HttpResponse response=client.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
			{
				Source source=new Source(response.getEntity().getContent());
				//�ⲿ���ǻ�ȡ���������
				List<Element> elements=source.getAllElementsByClass("topic-doc");
				for(int i=0;i<elements.size();i++)
				{
					String name,title,time;
					Topic topic=new Topic();
					//Log.d("element", "-------------------------");
					List<Element> el=elements.get(i).getAllElements("span");
					for(int j=0;j<el.size();j++)
					{
						if("color-green".equals(el.get(j).getAttributeValue("class")))
						{
							//ʱ��
							time=el.get(j).getContent().toString();
							topic.setLastdate(time);
						}
						if("pl20".equals(el.get(j).getAttributeValue("class")))
						{
							List<Element> elee=el.get(j).getAllElements("a");
							for(Element e:elee)
							{
								//��������
								name=e.getContent().toString();
								topic.setAuthor(name);
							}
						}
					}
					
					List<Element> ele=elements.get(i).getAllElements("p");
					for(int j=0;j<ele.size();j++)
					{
						title=ele.get(j).getContent().toString();
//						List<Element> e3=ele.get(j).getAllElements("a");
//						String href="";
//						for(Element e:e3)
//						{
//							href=e.getContent().toString();
//							
//						}
						
						topic.setTitle(title);
					}
					list.add(topic);
				}
				
				//������������ǻ�ȡ���������
				List<Element> elements2=source.getAllElements("li");
				for(int i=0;i<elements2.size();i++)
				{
					
				//	Log.d("element","-------------------------------");
					if("clearfix".equals(elements2.get(i).getAttributeValue("class")))
					{
						Topic topic=new Topic();
						String name,title,time;
						List<Element> ele=elements2.get(i).getAllElements("h4");
						for(int j=0;j<ele.size();j++)
						{
							if(j==0)
							{
								//ʱ��
								time=ele.get(j).getContent().toString().substring(0, 19);
								topic.setLastdate(time);
							}
							List<Element> ele2=ele.get(j).getAllElements("a");
							for(Element e:ele2)
							{
								//����
								name=e.getContent().toString(); 
								topic.setAuthor(name);
							}
						}
						List<Element> ele2=elements2.get(i).getAllElements("p");
						for(int j=0;j<ele2.size();j++)
						{
							//����
							title=ele2.get(j).getContent().toString();
							topic.setTitle(title);
							//Log.d("element", "]]]"+title);
						}
						list.add(topic);
					}
					
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	Log.d("element", list.size()+"");
		return list;
	}
	
	/**
	 * ������
	 * @param title
	 * @param content
	 * @param url
	 * @return
	 */
	public boolean publishTopic(String title,String content,String url)
	{
		HttpPost httpRequest=new HttpPost(url);
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("rev_title", title));
		params.add(new BasicNameValuePair("rev_text", content));
		params.add(new BasicNameValuePair("ck", "a06L"));
		params.add(new BasicNameValuePair("rev_submit", "���ˣ�����"));
		
		try {
			//�����ַ���
			HttpEntity httpEntity=new UrlEncodedFormEntity(params,"utf-8");
			httpRequest.setEntity(httpEntity);
			//ȡ��Ĭ�ϵ�HttpClient
			DefaultHttpClient httpClient=new DefaultHttpClient();
			httpClient.setCookieStore(ckStore);
			//ȡ��HttpResponse
			try {
				HttpResponse httpResponse=httpClient.execute(httpRequest);
				//�鿴�Ƿ����ӳɹ�
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
				{
					//ȡ�÷��ص��ַ���
					String str=	EntityUtils.toString(httpRequest.getEntity());
					Log.d("return", str);
					return true;
				}
				else
				{
					Log.d("return", "fail");
					return false;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//	Toast.makeText(context, text, duration)
				
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * �ظ�����
	 * @param content
	 * @param url
	 * @return
	 */
	public boolean topicReply(String content,String url)
	{
		HttpPost httpPost=new HttpPost(url);
		List<NameValuePair> params=new ArrayList<NameValuePair> ();
		params.add(new BasicNameValuePair("rv_comment", content));
		params.add(new BasicNameValuePair("ck","a06L"));
		params.add(new BasicNameValuePair("start","0"));
		//�����ַ���
		try {
			HttpEntity entity=new UrlEncodedFormEntity(params,"utf-8");
			httpPost.setEntity(entity);
			//ȡ��Ĭ��HttpClien
			DefaultHttpClient httpClient=new DefaultHttpClient();
			httpClient.setCookieStore(ckStore);
			//ȡ��HttpResponse
			try {
				HttpResponse httpResponse=httpClient.execute(httpPost);
				//�������Ƿ�ɹ�
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
				{
					String str=EntityUtils.toString(httpResponse.getEntity());
					Log.d("e", str);
					return true;
				}
				else
				{
					Log.d("e","topic reply false");
					return false;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}


