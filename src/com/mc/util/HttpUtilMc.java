package com.mc.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * @author Administrator
 * @description �ǵ��޸Ĵ��룬���ͷ�������Ӧ��ʱ����Ҫ fall back ����
 */

public class HttpUtilMc {
	// ����URL
	public static final String IP = "http://www.xiyoumc.org";
	public static final String BASE_URL = IP + "/HowOld/";
	public static String SERVER_ADDRESS = "120.25.124.226";
	public static final String XUPT_IP1 = "120.25.124.226";
	public static final String XUPT_IP2 = "120.25.124.226";
	/*
	 * public static String SERVER_ADDRESS="192.168.11.1"; public static int
	 * SERVER_PORT = 8080;
	 */

	public static String CONNECT_EXCEPTION = "�������쳣�������µ�¼";
	public static String CONNECT_REPEAT_EXCEPTION = "�������쳣���������µ�¼";

	// ���Get�������request
	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	// ���Post�������request
	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}

	// ������������Ӧ����response
	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
		// ��ȡ��ʱ
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);
		HttpResponse response = client.execute(request);
		return response;
	}

	/**
	 * ������������Ӧ����response
	 * 
	 * @param request
	 *            request
	 * @return httpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
		// ��ȡ��ʱ
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 6000);
		HttpResponse response = client.execute(request);
		return response;
	}

	// ����Post���󣬻����Ӧ��ѯ���
	public static String queryStringForPost(String url) {
		// ����url���HttpPost����
		// for test remove , if run server ,need fall back
		HttpPost request = HttpUtilMc.getHttpPost(url);
		String result = null;
		// for test remove , if run server ,need fall back
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtilMc.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			if (response.getStatusLine().getStatusCode() == 200) {

				// �����Ӧ
				result = EntityUtils.toString(response.getEntity(), "utf-8");// ��ֹ��������
				// result=new String(result.getBytes("ISO-8859-1"),"utf-8"); //
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;

		// for test add , if run server ,need remove
		// return String.valueOf(1);
	}

	// �����Ӧ��ѯ���
	public static String queryStringForPost(HttpPost request) {
		String result = null;
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtilMc.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			if (response.getStatusLine().getStatusCode() == 200) {
				// �����Ӧ
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;
	}

	// ����Get���󣬻����Ӧ��ѯ���
	public static String queryStringForGet(String url) {
		// ���HttpGet����
		HttpGet request = HttpUtilMc.getHttpGet(url);
		String result = null;
		try {
			// �����Ӧ����
			HttpResponse response = HttpUtilMc.getHttpResponse(request);
			// �ж��Ƿ�����ɹ�
			if (response.getStatusLine().getStatusCode() == 200) {
				// �����Ӧ
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;
	}

	/**
	 * ��֤ѧУ�������Ƿ����pingͨ
	 * 
	 * @param timeout
	 * @return
	 */
	private static boolean IsReachIP(String ip) {
		try {
			return Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip)
					.waitFor() == 0 ? true : false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean IsReachIP() {
		return true/* IsReachIP(XUPT_IP1) ? true : IsReachIP(XUPT_IP2) */;
	}
}
