package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Konfirmasi extends Activity {
	String ip="",myPosisi,myLati,myLongi;
	String upload="";
	String id_reservasi;
	String id_reservasi0="";

	EditText txtid_reservasi;
	EditText txttanggal;
	EditText txtjumlah;
	EditText txtjam_datang;

	EditText txtpesan;
	EditText txtnominal;

	String nominal,nominal2;
	Button btnProses;
	Button btnHapus;

	String id_customer,nama_customer;
	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_id_customer = "id_customer";
	private static final String TAG_tanggal = "tanggal";
	private static final String TAG_jam = "jam";
	private static final String TAG_jumlah = "jumlah";
	private static final String TAG_jam_datang = "jam_datang";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.konfirmasi);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Konfirmasi.this);
		Boolean Registered = sharedPref.getBoolean("Registered", false);
		if (!Registered) {
			finish();
		} else {
			id_customer = sharedPref.getString("id_customer", "");
			nama_customer = sharedPref.getString("nama_customer", "");
			id_reservasi = sharedPref.getString("id_reservasi", "");
		}
		ip=jsonParser.getIP();
		Intent i = getIntent();


		txtid_reservasi= (EditText) findViewById(R.id.txtid_reservasi);
		txttanggal= (EditText) findViewById(R.id.txttanggal);
		txtjumlah= (EditText) findViewById(R.id.txtjumlah);
		txtjam_datang= (EditText) findViewById(R.id.txtjam_datang);

		txtpesan= (EditText) findViewById(R.id.txtpesan);
		txtnominal= (EditText) findViewById(R.id.txtnominal);



		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_reservasi= i.getStringExtra("pk");
		upload= i.getStringExtra("upload");
		nominal= i.getStringExtra("nominal");
		nominal2= i.getStringExtra("nominal2");
		txtnominal.setText(nominal);

		btnProses.setText("Konfirmasi");
		btnHapus.setText("Kembali");
		new get().execute();

		Button  btnupload=(Button)findViewById(R.id.btnupload);
		btnupload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Konfirmasi.this,UploadToServer.class);
				i.putExtra("id_reservasi", id_reservasi);
				i.putExtra("nominal", nominal);
				i.putExtra("nominal2", nominal2);
				startActivity(i);
				finish();
			}});

		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(upload.equalsIgnoreCase("avatar.jpg")){
					gagal("Upload");
				}else{
					new save().execute();
				}
			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			finish();
			}});

		if(upload.equalsIgnoreCase("")){
			upload="avatar.jpg";
			String arUrlFoto=ip+"ypathfile/"+upload;
			new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);
		}
		else{
			btnupload.setText("UPLOAD "+upload);
			String arUrlFoto=ip+"ypathfile/"+upload;
			new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);
		}

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
			pDialog = new ProgressDialog(Konfirmasi.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_reservasi", id_reservasi));

				String url=ip+"reservasi/reservasi_detail.php";
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
								txtid_reservasi.setText(myJSON.getString("id_reservasi"));
								txttanggal.setText(myJSON.getString(TAG_tanggal));
								txtjumlah.setText(myJSON.getString(TAG_jumlah));
								txtjam_datang.setText(myJSON.getString(TAG_jam_datang));




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
		protected void onPostExecute(String file_url) {pDialog.dismiss();}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class save extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Konfirmasi.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lpesan= txtpesan.getText().toString();
			String lnominal= txtnominal.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_customer", id_customer));
			params.add(new BasicNameValuePair("pesan", lpesan));
			params.add(new BasicNameValuePair("nominal", nominal2));
			params.add(new BasicNameValuePair("bukti_bayar", upload));

			String url=ip+"konfirmasi/konfirmasi_add.php";
			Log.v("add",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				 sukses= json.getInt(TAG_SUKSES);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {
				final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Konfirmasi.this);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("Registered", true);
				editor.putString("id_reservasi", id_reservasi);
				editor.apply();
				sukses("Simpan");
			} else { gagal("Simpan");}
		}
	}

	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Lengkapi Data")
				.setMessage("Silakan lengkapi data "+item +" !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}

	public void sukses(String item){
		new AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

						Intent i=new Intent(Konfirmasi.this,Menu_utama.class);
						finish();
						startActivity(i);
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Gagal")
				.setMessage("Silahkan "+item +" Bukti Pembayaran !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
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
