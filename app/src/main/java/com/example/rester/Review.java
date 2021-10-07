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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Review extends Activity {
	String ip="",myPosisi,myLati,myLongi;
	String gambar="";
	String id_restaurant;
	String id_restaurant0="";
	String id_reservasi;

	TextView txtnama_restaurant;
	TextView txtdeskripsi;
	TextView txtalamat;
	TextView txtpesan;
	Button btnProses;
	Button btnHapus;
	String id_customer,nama_customer;
	int sukses;
RatingBar simpleRatingBar;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_nama_restaurant = "nama_restaurant";
	private static final String TAG_email = "email";
	private static final String TAG_telepon = "telepon";
	private static final String TAG_alamat = "alamat";
	private static final String TAG_gambar = "gambar";
	private static final String TAG_deskripsi = "deskripsi";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review);


		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Review.this);
		Boolean Registered = sharedPref.getBoolean("Registered", false);
		if (!Registered) {
			finish();
		} else {
			id_customer = sharedPref.getString("id_customer", "");
			nama_customer = sharedPref.getString("nama_customer", "");
		}

		ip=jsonParser.getIP();
		Intent i = getIntent();

		simpleRatingBar = (RatingBar) findViewById(R.id.ratingbar);


		txtnama_restaurant= (TextView) findViewById(R.id.txtnama_restaurant);
		txtalamat= (TextView) findViewById(R.id.txtalamat);
		txtdeskripsi= (TextView) findViewById(R.id.txtdeskripsi);
		txtpesan= (TextView) findViewById(R.id.txtpesan);

		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_restaurant0 = i.getStringExtra("id_restaurant");
		id_reservasi = i.getStringExtra("id_reservasi");
		id_restaurant=id_restaurant0;
			new get().execute();
			btnProses.setText("Submit Review");
			btnHapus.setText("Finish");
			btnHapus.setVisibility(View.VISIBLE);


			btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new save().execute();
			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Review.this, Menu_utama.class);
					finish();//	new del().execute();
				startActivity(i);


			}});


	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Review.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_restaurant", id_restaurant0));

				String url=ip+"restaurant/restaurant_detail.php";
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
								txtnama_restaurant.setText(myJSON.getString(TAG_nama_restaurant));
								txtalamat.setText(myJSON.getString(TAG_alamat));
								gambar=myJSON.getString("gambar");
								txtdeskripsi.setText(myJSON.getString(TAG_deskripsi));




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
			String arUrlFoto=ip+"ypathfile/"+gambar;
			new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);

		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

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
String msg;
	class save extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Review.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lbintang= String.valueOf(simpleRatingBar.getRating());
			String lpesan= txtpesan.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_restaurant", id_restaurant));
			params.add(new BasicNameValuePair("id_customer", id_customer));

			params.add(new BasicNameValuePair("bintang", lbintang));
			params.add(new BasicNameValuePair("pesan", lpesan));
			String url=ip+"review/review_add.php";
			Log.v("add",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				sukses= json.getInt(TAG_SUKSES);
				msg= json.getString("pesan");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {
				sukses("Simpan");
			} else { gagal(msg);}
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
						finish();
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Failed")
				.setMessage(item)
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
