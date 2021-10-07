package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Profile extends Activity {
	String ip="",myPosisi,myLati,myLongi;
	String upload,gambar;
	String id_customer;
	String id_customer0="";

	EditText txtnama_customer;
	EditText txtemail;
	EditText txttelepon;
	EditText txtalamat;
	EditText txtusername;
	EditText txtpassword;

	TextView tvnama_customer;
	TextView tvemail;

	Button btnProses;
	Button btnHapus;

	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_nama_customer = "nama_customer";
	private static final String TAG_email = "email";
	private static final String TAG_telepon = "telepon";
	private static final String TAG_alamat = "alamat";
	private static final String TAG_username = "username";
	private static final String TAG_password = "password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		ip=jsonParser.getIP();
		Intent i = getIntent();

		txtnama_customer= (EditText) findViewById(R.id.txtnama_customer);
		txtemail= (EditText) findViewById(R.id.txtemail);
		txttelepon= (EditText) findViewById(R.id.txttelepon);
		txtalamat= (EditText) findViewById(R.id.txtalamat);
		txtusername= (EditText) findViewById(R.id.txtusername);
		txtpassword= (EditText) findViewById(R.id.txtpassword);
		txttelepon= (EditText) findViewById(R.id.txttelepon);

		tvnama_customer= (TextView) findViewById(R.id.tvnama_customer);
		tvemail= (TextView) findViewById(R.id.tvemail);



		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_customer0 = i.getStringExtra("pk");
		upload = i.getStringExtra("upload");
		id_customer=id_customer0;
			new get().execute();
			btnProses.setText("Update Profile");
			btnHapus.setText("Back");
			btnHapus.setVisibility(View.VISIBLE);


		ImageView btnubah=(ImageView)findViewById(R.id.btnubah);
		btnubah.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Profile.this,UploadToServer2.class);
				i.putExtra("id_customer", id_customer);
				startActivity(i);
				finish();
			}});


		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String lnama_customer= txtnama_customer.getText().toString();
				String lemail= txtemail.getText().toString();
				String ltelepon= txttelepon.getText().toString();
				String lalamat= txtalamat.getText().toString();
				String lusername= txtusername.getText().toString();
				String lpassword= txtpassword.getText().toString();

				 if(lnama_customer.length()<1){lengkapi("Customer Name");}
				else if(ltelepon.length()<1){lengkapi("telepon");}
				else if(lemail.length()<1){lengkapi("email");}
				else{
						new update().execute();

				}//else

			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			finish();
			}});



	}

	//fungsi Download image task
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			}
			catch (Exception e) {Log.e("Error", e.getMessage());e.printStackTrace();}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result); }
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Profile.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_customer", id_customer0));

				String url=ip+"customer/customer_detail.php";
				Log.v("detail",url);
				JSONObject json = jsonParser.makeHttpRequest(url, "GET", params1);
				Log.d("detail", json.toString());
				sukses = json.getInt(TAG_SUKSES);
				if (sukses == 1) {
					JSONArray myObj = json.getJSONArray(TAG_record); // JSON Array
					final JSONObject myJSON = myObj.getJSONObject(0);
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								txtnama_customer.setText(myJSON.getString(TAG_nama_customer));
								txtemail.setText(myJSON.getString(TAG_email));
								tvnama_customer.setText(myJSON.getString(TAG_nama_customer));
								tvemail.setText(myJSON.getString(TAG_email));
								txttelepon.setText(myJSON.getString(TAG_telepon));
								txtalamat.setText(myJSON.getString(TAG_alamat));
								txtusername.setText(myJSON.getString(TAG_username));
								txtpassword.setText(myJSON.getString(TAG_password));
								gambar=myJSON.getString("gambar");



							}
							catch (JSONException e) {e.printStackTrace();}
						}});
				}
				else{
					// jika id tidak ditemukan
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if(upload.equalsIgnoreCase("")){
				String arUrlFoto=ip+"ypathfile/"+gambar;
				new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);
			}
			else{
				String arUrlFoto=ip+"ypathfile/"+upload;
				new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);
			}
		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	


	class update extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Profile.this);
			pDialog.setMessage("Mengubah data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lnama_customer= txtnama_customer.getText().toString();
			String lemail= txtemail.getText().toString();
			String ltelepon= txttelepon.getText().toString();
			String lalamat= txtalamat.getText().toString();
			String lusername= txtusername.getText().toString();
			String lpassword= txtpassword.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_customer", id_customer));
			params.add(new BasicNameValuePair("nama_customer", lnama_customer));
			params.add(new BasicNameValuePair("email", lemail));
			params.add(new BasicNameValuePair("telepon", ltelepon));
			params.add(new BasicNameValuePair("alamat", lalamat));
			params.add(new BasicNameValuePair("username", lusername));
			params.add(new BasicNameValuePair("password", lpassword));
			params.add(new BasicNameValuePair("gambar", upload));

			String url=ip+"customer/customer_update.php";
			Log.v("update",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				 sukses = json.getInt(TAG_SUKSES);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {sukses("Update");
			} else { gagal("Update");}
		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class del extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Profile.this);
			pDialog.setMessage("Menghapus data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_customer", id_customer0));

				String url=ip+"customer/customer_del.php";
				Log.v("delete",url);
				JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
				Log.d("delete", json.toString());
				sukses = json.getInt(TAG_SUKSES);

			}
			catch (JSONException e) {e.printStackTrace();}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {sukses("Hapus");
			} else { gagal("Hapus");}}
	}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	


	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Invalid")
				.setMessage("Please input "+item +" !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}

	public void lengkapi2(String item){
		new AlertDialog.Builder(this)
				.setTitle("Failed")
				.setMessage("Email Not Valid !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}
	public void sukses(String item){
		new AlertDialog.Builder(this)
				.setTitle("Success")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
						finish();
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Failed")
				.setMessage("Failed "+item +" Data !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
