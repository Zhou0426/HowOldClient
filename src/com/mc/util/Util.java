package com.mc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;

public class Util {

	// ��ά���ļ�
	public static final String QRCODE_FILENAME = "/���ʳɼ�.png";
	private final static String TAG = "util";
	public final static String infosFloder = "/xuptscore/devInfos";
	public final static String LOGINFILE = "login.log";

	

	/**
	 * �����洢�豸��Ϣ���쳣��Ϣ Map<String,String> : mLogInfo
	 * 
	 * @since 2013-3-21����8:46:15
	 */
	/**
	 * getDeviceInfo:{��ȡ�豸������Ϣ} ��������������������������������������������������������������������
	 * 
	 * @param paramContext
	 * @throws
	 * @since I used to be a programmer like you, then I took an arrow in the
	 *        knee��Ver 1.0
	 *        ������������������������������������������������������������������������������������������������������������
	 *        ������������������������������������������������������������������������������������������������
	 *        2013-3-24����12:30:02 Modified By Norris
	 *        ��������������������������������������������������������
	 *        ������������������������������������������������������������������������������
	 *        ����������������������������������������������������������������������
	 */
	@SuppressWarnings("static-access")
	public static void saveDeviceInfo(Context paramContext) {

		Map<String, String> mLogInfo = new HashMap<String, String>();
		try {
			// ��ð�������
			PackageManager mPackageManager = paramContext.getPackageManager();
			// �õ���Ӧ�õ���Ϣ������Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName;
				String versionCode = mPackageInfo.versionCode + "";
				mLogInfo.put("versionName", versionName);
				mLogInfo.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// �������
		Field[] mFields = Build.class.getDeclaredFields();
		// ����Build���ֶ�key-value �˴�����Ϣ��Ҫ��Ϊ���ڷ��������ֻ����ְ汾�ֻ������ԭ��
		for (Field field : mFields) {
			try {
				field.setAccessible(true);
				mLogInfo.put(field.getName(), field.get("").toString());
				Log.d(TAG, field.getName() + ":" + field.get(""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		StringBuffer mStringBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			mStringBuffer.append(key + "=" + value + "\r\n");
		}

		String mFileName = ((TelephonyManager) paramContext
				.getSystemService(paramContext.TELEPHONY_SERVICE))
				.getDeviceId()
				+ ".log";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File mDirectory = new File(
						Environment.getExternalStorageDirectory() + infosFloder);

				Log.v(TAG, mDirectory.toString());
				if (!mDirectory.exists())
					mDirectory.mkdir();
				FileOutputStream mFileOutputStream = new FileOutputStream(
						mDirectory + "/" + mFileName);
				mFileOutputStream.write(mStringBuffer.toString().getBytes());
				mFileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static long lastClickTime;

	/**
	 * ��ֹ��ť������ܶ��
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH/mm/ss");
		return sdf.format(new Date());
	}

	/**
	 * �����Ƿ� �ͻ�����Ϣ�ϴ��������� ���ҽ� ��¼��Ϣ ���뵽 �ļ���
	 * 
	 * @param paramContext
	 * @return
	 */
	public static boolean isRecordLoginMessage(Context paramContext) {

		@SuppressWarnings("static-access")
		File loginMsgLogFile = new File(
				Environment.getExternalStorageDirectory()
						+ infosFloder
						+ "/"
						+ ((TelephonyManager) paramContext
								.getSystemService(paramContext.TELEPHONY_SERVICE))
								.getDeviceId() + LOGINFILE);
		try {
			if (!loginMsgLogFile.exists()) {
				loginMsgLogFile.createNewFile();
				saveLoginAppVersion(paramContext);
				return false;
			}
			saveLoginAppVersion(paramContext);
			return true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * ���û���¼��Ϣд����־
	 * 
	 * @param loginMsgLogFile
	 * @param msg
	 * @param isAppend
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void writeLoginMsgToLog(File loginMsgLogFile, String msg,
			boolean isAppend) {
		try {
			FileOutputStream fos = new FileOutputStream(loginMsgLogFile,
					isAppend);
			fos.write(msg.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private static void saveLoginAppVersion(Context paramContext) {
		try {
			// ��ð�������
			PackageManager mPackageManager = paramContext.getPackageManager();
			// �õ���Ӧ�õ���Ϣ������Activity
			PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
					paramContext.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			StringBuffer sb = new StringBuffer();
			if (mPackageInfo != null) {
				String versionName = mPackageInfo.versionName == null ? "null"
						: mPackageInfo.versionName;
				String versionCode = mPackageInfo.versionCode + "";
				sb.append(getTime() + "\t" + versionCode + "--" + versionName
						+ "\n");
			}
			writeLoginMsgToLog(
					new File(
							Environment.getExternalStorageDirectory()
									+ infosFloder
									+ "/"
									+ ((TelephonyManager) paramContext
											.getSystemService(paramContext.TELEPHONY_SERVICE))
											.getDeviceId() + LOGINFILE),
					sb.toString(), true);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			Log.e("getAndroidSDKVersion", e.toString());
		}
		return version;
	}

	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	// �������ȡͼƬ,�������ҵ�����
	public static Bitmap getBitmap(String pictureUrl) {
		URL url = null;
		Bitmap bitmap = null;

		InputStream in = null;
		try {
			if (pictureUrl != null) {
				url = new URL(pictureUrl);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setConnectTimeout(6000);// ����ӳ�6000����
				httpURLConnection.setDoInput(true);// ���ӻ�ȡ������
				httpURLConnection.setUseCaches(true);// �û���
				in = httpURLConnection.getInputStream();
				bitmap = BitmapFactory.decodeStream(in);
			} else {
				return null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return bitmap;

	}

	// ���ļ�ת��ΪͼƬ
	public static Bitmap convertToBitmap(String path, int w, int h) {
		if (!new File(path).exists()) {
			return null;
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// ����Ϊtureֻ��ȡͼƬ��С
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// ����Ϊ��
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// ����
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	private static boolean haveChar(String str) {
		try {
			Integer.valueOf(str);// ���ַ���ǿ��ת��Ϊ����
			return false;// ��������֣�����True
		} catch (Exception e) {
			return true;// ����׳��쳣������False
		}

	}

	public static boolean isNull(Object s) {
		return s == null ? true : false;
	}

	public static boolean checkPWD(String pwd) {
		return pwd
				.matches("^(?![A-Z]*$)(?![a-z]*$)(?![0-9]*$)(?![^a-zA-Z0-9]*$)\\S+$");
	}

	/**
	 * check url requestData
	 * 
	 * @param data
	 * @param viewstate
	 * @return
	 */
	public static boolean checkRankRequestData(String data, String time) {
		// TODO Auto-generated method stub
		String realTime = "";
		try {
			realTime = Passport.jiemi(data,
					String.valueOf(new char[] { 2, 4, 8, 8, 1, 1 }));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if (realTime.equals(time)) {
			return true;
		}
		return false;
	}

	public static boolean isDebugable(Context context) {
		try {
			ApplicationInfo info = context.getApplicationInfo();
			return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Exception e) {
		}
		return false;
	}

	private static boolean hasDigit(String content) {

		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())
			flag = true;
		return flag;

	}

	public static boolean hasDigitAndNum(String str) {
		if (haveChar(str) && hasDigit(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ȡ����汾��
	 */
	public static String getVersion(Context context) {
		String version = "";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pt = pm.getPackageInfo(context.getPackageName(), 0);
			version = pt.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
}
