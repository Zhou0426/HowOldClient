package com.mc.howoldareu;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mc.util.BitmapUtil;
import com.mc.util.HttpAssist;
import com.mc.util.Passport;
import com.mc.util.StaticVarUtil;
import com.mc.util.Util;
import com.mc.util.ViewUtil;

public class MainActivity extends Activity {

	private Button selectPhotoBtn;
	private ProgressDialog progressDialog;
	private static final int PIC = 11;// ͼƬ
	private static final int PHO = 22;// ����
	private static final int RESULT = 33;// ���ؽ��
	private ArrayList<Faces> list = new ArrayList<Faces>();
	private View[] views;
	private Context context;
	private Activity activity;
	private WebView webview_evacuation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;
		activity = this;
		this.progressDialog = ViewUtil.getProgressDialog(MainActivity.this,
				"���ڷ���");
		if (StaticVarUtil.PATH == null) {
			if (Util.isExternalStorageWritable()) {
				StaticVarUtil.PATH = "/sdcard/howold/";// �����ļ�Ŀ¼
			} else {
				StaticVarUtil.PATH = "/data/data/com.xy.fy.main/";// �����ļ�Ŀ¼
			}
			if (!new File(StaticVarUtil.PATH).exists()) {
				new File(StaticVarUtil.PATH).mkdirs();
			}
		}

		if (!new File(StaticVarUtil.PATH, "main006" + ".JPEG").exists()) {
			BitmapUtil.saveBitmapToFile(BitmapFactory.decodeResource(
					MainActivity.this.getResources(), R.drawable.main006),
					StaticVarUtil.PATH, "main006" + ".JPEG");
		}
		showLocalImage("main006");
		selectPhotoBtn = (Button) findViewById(R.id.selectPhoto);

		selectPhotoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				chooseHeadPhoto();
			}
		});
	}

	/**
	 * 
	 * webview ��ʾ����ͼƬ������Ӧ���ִ�С��ͼƬ������
	 */
	private void showLocalImage(String fileName) {

		LayoutInflater inflate = LayoutInflater.from(getApplicationContext());
		webview_evacuation = (WebView) findViewById(R.id.webview_evacuation);

		final LinearLayout ly_map = (LinearLayout) findViewById(R.id.layoutImage);
		File dir = new File("mnt/" + StaticVarUtil.PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}

		final String imageUrl = "file://mnt/" + StaticVarUtil.PATH + "/"
				+ fileName + ".JPEG";
		ViewTreeObserver vto2 = ly_map.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ly_map.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				// ��ȡҪ��ʾͼƬ�Ĳ��ֿ��
				int h = ly_map.getHeight();
				int w = ly_map.getWidth();
				;

				String data = "<HTML><IMG src=\"" + imageUrl + "\"" + "width="
						+ w + "height=" + h + "/>";
				webview_evacuation.loadDataWithBaseURL(imageUrl, data,
						"text/html", "utf-8", null);

				// webview_evacuation.loadUrl(imageUrl);//ֱ����ʾ����ͼƬ
				webview_evacuation.getSettings().setBuiltInZoomControls(true); // ��ʾ�Ŵ���С
																				// controler
				webview_evacuation.getSettings().setSupportZoom(true); // ��������
				webview_evacuation.setSaveEnabled(true);
			}
		});
	}

	/**
	 * searchFile �����ļ������뵽ArrayList ����ȥ
	 * 
	 * @String keyword ���ҵĹؼ���
	 * @File filepath ���ҵ�Ŀ¼
	 * */
	public boolean searchFile(String keyword, File filepath) {
		Locale defloc = Locale.getDefault();

		try {
			if (!filepath.exists()) {
				filepath.mkdir();
			}
			// �ж�SD���Ƿ����
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File[] files = filepath.listFiles();
				if (files.length > 0) {
					for (File file : files) {
						if (file.isDirectory()) {
							// ���Ŀ¼�ɶ���ִ�У�һ��Ҫ�ӣ���Ȼ��ҵ���
							if (file.canRead()) {
								searchFile(keyword, file); // �����Ŀ¼���ݹ����
							}
						} else {
							// �ж����ļ���������ļ����ж�
							try {
								if (file.getName().indexOf(keyword) > -1
										|| file.getName().indexOf(
												keyword.toUpperCase(defloc)) > -1) {
									// �����ļ�
									return true;
								}
							} catch (Exception e) {
								return false;
							}
						}
					}
				}
			}

		} catch (Exception e) {
		}

		return false;
	}

	/*
	 * ȡ�ûش�������
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// �������벻��ȡ����ʱ��
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case PHO:
				File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case PIC:
				// ��Ƭ��ԭʼ��Դ��ַ
				Uri originalUri = data.getData();
				startPhotoZoom(originalUri);
				break;
			case RESULT:
				if (data != null) {
					// Bundle extras = data.getExtras();
					Uri uri = data.getData();

					ContentResolver cr = this.getContentResolver();
					Bitmap bitmap;
					try {
						bitmap = BitmapFactory.decodeStream(cr
								.openInputStream(uri));
						rankRequestParmas();
						// bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 240);//
						// ������Ϊ240���ص�ͼƬ
						BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
								StaticVarUtil.TIME + ".JPEG");
						// image.setScaleType(ImageView.ScaleType.FIT_XY);//
						// ���������Ļ

						UploadFileAsytask uploadFileAsytask = new UploadFileAsytask();
						progressDialog.show();
						uploadFileAsytask
								.execute(new String[] { StaticVarUtil.PATH
										+ "/" + StaticVarUtil.TIME + ".JPEG" });
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void rankRequestParmas() {
		StaticVarUtil.TIME = System.currentTimeMillis();
		// String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
		try {

			String realData = Passport.jiami(
					String.valueOf(StaticVarUtil.TIME),
					String.valueOf(new char[] { 2, 4, 8, 8, 1, 1 }));
			realData = URLEncoder.encode(realData);
			StaticVarUtil.DATA = realData;
			if (!Util.checkRankRequestData(realData,
					String.valueOf(StaticVarUtil.TIME))) {
				rankRequestParmas();// �ݹ��ٴμ��㣬ֱ���������ȷ��
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class UploadFileAsytask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpAssist.uploadFile(new File(params[0]));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			try {
				if (result.equals("error")) {
					return;
				}
				Drawable drawable = new BitmapDrawable(
						BitmapFactory.decodeFile(StaticVarUtil.PATH + "/"
								+ StaticVarUtil.TIME + ".JPEG"));

				JSONObject jsonObject = new JSONObject(result);
				JSONArray jsonArray = (JSONArray) jsonObject.get("Faces");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject o = (JSONObject) jsonArray.get(i);

					JSONObject faceRectangleJO = (JSONObject) o
							.getJSONObject("faceRectangle");
					JSONObject attributesJO = (JSONObject) o
							.getJSONObject("attributes");

					FaceRectangle faceRectangle = new FaceRectangle.Builder()
							.setTop(dip2px(faceRectangleJO.getInt("top")))
							.setHeight(
									dip2px(faceRectangleJO.getInt("height") + 50))
							.setLeft(dip2px(faceRectangleJO.getInt("left")))
							.setWidth(dip2px(faceRectangleJO.getInt("width")))
							.create();
					Attributes attributes = new Attributes.Builder()
							.setAge(attributesJO.getInt("age"))
							.setGender(attributesJO.getString("gender"))
							.Create();

					Faces faces = new Faces.Builder().setAttributes(attributes)
							.setFaceRectangle(faceRectangle).create();
					list.add(faces);
				}

				if (list.size() == 0) {
					new AlertDialog.Builder(getApplicationContext())
							.setMessage("Couldn��t detect any faces.")
							.setPositiveButton("Re selection",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											chooseHeadPhoto();
										}

									}).show();
					return;
				}
				views = new View[list.size()];
				int i = 0;
				for (Faces face : list) {
					Attributes attribute = face.attributes;
					FaceRectangle faceRectangle = face.faceRectangle;
					AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(
							faceRectangle.width, faceRectangle.height,
							faceRectangle.left, faceRectangle.top);
					LayoutInflater inflate = LayoutInflater
							.from(getApplicationContext());
					View view = inflate.inflate(R.layout.face_item, null);
					((TextView) view.findViewById(R.id.age))
							.setText(attribute.age + "");
					((ImageView) view.findViewById(R.id.type))
							.setImageDrawable(getApplicationContext()
									.getResources()
									.getDrawable(
											"Female".equals(attribute.gender) ? R.drawable.icon_gender_female
													: R.drawable.icon_gender_male));

					System.out.println("w:" + faceRectangle.width + " h:"
							+ faceRectangle.height + " :" + faceRectangle.left
							+ " y:" + faceRectangle.top);
					((AbsoluteLayout) webview_evacuation).addView(view,
							layoutParams);

					views[i] = view;
					i++;
				}
				
				LoadFile loadFile = new LoadFile();
				loadFile.execute();
			} catch (Exception e) {
				Log.i("LoginActivity", e.toString());
			}
		}
	}

	class LoadFile extends AsyncTask<String, String, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			showLocalImage(StaticVarUtil.TIME + "");
			BitmapUtil.saveBitmapToFile(captureWebview(), StaticVarUtil.PATH,
					StaticVarUtil.TIME + ".JPEG");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			progressDialog.cancel();
			if (result) {
				showLocalImage(StaticVarUtil.TIME + "");
			}
			super.onPostExecute(result);
		}

	}

	/**
	 * ��ȡ������ҳ
	 */
	private Bitmap captureWebview() {
		Picture picture = webview_evacuation.capturePicture();
		Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(),
				picture.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		picture.draw(c);
		return bitmap;

	}

	private int dip2PxScale(int dip, float scale) {
		return (int) (dip2px(dip) * scale);
	}

	private int dip2px(float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	private int dip2px(int dip) {
		int px = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources()
						.getDisplayMetrics()));
		return px;
	}

	/*
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");// ����ϵͳ�Ľ�ͼ���ܡ�
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "false");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("scale", true);// �ڱ�
		intent.putExtra("scaleUpIfNeeded", true);// �ڱ�
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT);
	}

	/*
	 * ѡ��ͷ��
	 * 
	 * @return
	 */
	protected void chooseHeadPhoto() {
		if (views != null && views.length != 0) {
			for (int i = 0; i < views.length; i++) {
				((AbsoluteLayout) webview_evacuation).removeView(views[i]);
			}
			new File(StaticVarUtil.PATH + "/" + StaticVarUtil.TIME + ".JPEG")
					.delete();
		}
		String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
		new AlertDialog.Builder(this)
				.setTitle("����ͷ��")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						switch (which) {
						case 0:// ѡ�񱾵�ͼƬ

							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							break;
						case 1:// ����
							intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
							Uri imageUri = Uri.fromFile(new File(
									StaticVarUtil.PATH, System
											.currentTimeMillis() + ".JPEG"));
							// ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
							intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							break;
						}

						startActivityForResult(intent, RESULT);
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	private static class FaceRectangle {

		private int top;
		private int left;
		private int width;
		private int height;

		public FaceRectangle(Builder builder) {
			// TODO Auto-generated constructor stub
			top = builder.getTop();
			left = builder.getLeft();
			width = builder.getWidth();
			height = builder.getHeight();
		}

		public static class Builder {
			private int top;
			private int left;
			private int width;
			private int height;

			public int getTop() {
				return top;
			}

			public Builder setTop(int top) {
				this.top = top;
				return this;
			}

			public int getLeft() {
				return left;
			}

			public Builder setLeft(int left) {
				this.left = left;
				return this;
			}

			public int getWidth() {
				return width;
			}

			public Builder setWidth(int width) {
				this.width = width;
				return this;
			}

			public int getHeight() {
				return height;
			}

			public Builder setHeight(int height) {
				this.height = height;
				return this;
			}

			public FaceRectangle create() {
				return new FaceRectangle(this);
			}

		}
	}

	private static class Attributes {

		private String gender;
		private int age;

		public Attributes(Builder builder) {
			// TODO Auto-generated constructor stub
			this.gender = builder.getGender();
			this.age = builder.getAge();
		}

		public static class Builder {
			private String gender;
			private int age;

			public String getGender() {
				return gender;
			}

			public Builder setGender(String gender) {
				this.gender = gender;
				return this;
			}

			public int getAge() {
				return age;
			}

			public Builder setAge(int age) {
				this.age = age;
				return this;
			}

			public Attributes Create() {
				return new Attributes(this);
			}
		}
	}

	private static class Faces {
		// private Strign
		private Attributes attributes;
		private FaceRectangle faceRectangle;

		public Faces(Builder builder) {
			// TODO Auto-generated constructor stub
			this.attributes = builder.getAttributes();
			this.faceRectangle = builder.getFaceRectangle();
		}

		public static class Builder {
			private Attributes attributes;
			private FaceRectangle faceRectangle;

			public Attributes getAttributes() {
				return attributes;
			}

			public Builder setAttributes(Attributes attributes) {
				this.attributes = attributes;
				return this;
			}

			public FaceRectangle getFaceRectangle() {
				return faceRectangle;
			}

			public Builder setFaceRectangle(FaceRectangle faceRectangle) {
				this.faceRectangle = faceRectangle;
				return this;
			}

			public Faces create() {
				return new Faces(this);
			}
		}
	}
}
