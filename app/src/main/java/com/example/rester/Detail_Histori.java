package com.example.rester;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Detail_Histori extends ListActivity {
	String ip="",myPosisi,myLati,myLongi;
	String gambar="";
	String id_restaurant;
	String id_restaurant0="";
	String id_customer,nama_customer,id_reservasi;
	TextView txtnama_restaurant;
	TextView txtalamat,txttotal;
String status;
	String total="",total2="";
	TimePickerDialog timePickerDialog;
	EditText txtjam_datang,txtjumlah;
	EditText txtid_reservasi;
	EditText txttanggal;
	EditText txtstatus;

	Button btnProses;
	Button btnHapus;
	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	JSONParser jParser = new JSONParser();
	JSONArray myJSON = null;
	
	ArrayList<HashMap<String, String>> arrayList;
	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";
	
	private static final String TAG_id_detail = "id_detail";
	private static final String TAG_atas = "atas";
	private static final String TAG_bawah = "bawah";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_histori);
		arrayList = new ArrayList<HashMap<String, String>>();
		ip=jParser.getIP();


		Intent i = getIntent();
		id_reservasi = i.getStringExtra("pk");

		txtnama_restaurant= (TextView) findViewById(R.id.txtnama_restaurant);
		txtalamat= (TextView) findViewById(R.id.txtalamat);
		txttotal= (TextView) findViewById(R.id.txttotal);


		txtid_reservasi= (EditText) findViewById(R.id.txtid_reservasi);
		txttanggal= (EditText) findViewById(R.id.txttanggal);
		txtjam_datang= (EditText) findViewById(R.id.txtjam_datang);
		txtjumlah= (EditText) findViewById(R.id.txtjumlah);
		txtstatus= (EditText) findViewById(R.id.txtstatus);

        try{
            arrayList.clear();
        }
        catch(Exception ee){}
		new get().execute();

		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		btnProses.setText("Konfirmasi");
		btnProses.setVisibility(View.GONE);
		btnHapus.setText("Kembali");

//		ListView lv = getListView();
//		lv.setOnItemClickListener(new OnItemClickListener() {

//		@Override
//		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//			String pk = ((TextView) view.findViewById(R.id.kode_k)).getText().toString();
//			Intent i = new Intent(getApplicationContext(), AddMenu.class);
//			i.putExtra("pk", pk);
//			startActivityForResult(i, 100);
//		}});

		btnProses.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), Review.class);
				i.putExtra("id_reservasi", id_reservasi);
				i.putExtra("id_restaurant", id_restaurant);
				finish();
				startActivityForResult(i, 100);

			}});
}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {// jika result code 100
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}
	private void showTimeDialog() {

		/**
		 * Calendar untuk mendapatkan waktu saat ini
		 */
		Calendar calendar = Calendar.getInstance();

		/**
		 * Initialize TimePicker Dialog
		 */
		timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				txtjam_datang.setText(hourOfDay+":"+minute);
			}
		}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
		DateFormat.is24HourFormat(this));
		timePickerDialog.show();
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Detail_Histori.this);
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
								txttanggal.setText(myJSON.getString("tanggal")+" "+myJSON.getString("jam"));
								txtjumlah.setText(myJSON.getString("jumlah"));
								txtjam_datang.setText(myJSON.getString("jam_datang"));
								txtstatus.setText(myJSON.getString("status"));
								txtnama_restaurant.setText(myJSON.getString("nama_restaurant"));
								txtalamat.setText(myJSON.getString("alamat"));
								gambar=myJSON.getString("gambar");
								status=myJSON.getString("status");
								id_restaurant=myJSON.getString("id_restaurant");




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
			new loadMenu().execute();
		if(status.equalsIgnoreCase("Selesai"));
			btnProses.setText("Review");
			btnProses.setVisibility(View.VISIBLE);
			btnHapus.setText("Kembali");

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
	
	
	String tot;
	class loadMenu extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Detail_Histori.this);
			pDialog.setMessage("Load data. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi",id_reservasi));
			JSONObject json = jParser.makeHttpRequest(ip+"detail/detail_show.php", "GET", params);
			Log.d("show: ", json.toString());
			try {
				int sukses = json.getInt(TAG_SUKSES);
				total = json.getString("total");
				tot = json.getString("total2");
				if (sukses == 1) {
					myJSON = json.getJSONArray(TAG_record);
					for (int i = 0; i < myJSON.length(); i++) {
						JSONObject c = myJSON.getJSONObject(i);
						String id_detail= c.getString(TAG_id_detail);
						String atas = c.getString(TAG_atas);
						String bawah = c.getString(TAG_bawah);

						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_id_detail, id_detail);
						map.put(TAG_atas, atas);
						map.put(TAG_bawah, bawah);

						arrayList.add(map);
					}
				} else {
//					Intent i = new Intent(getApplicationContext(),BkDetail.class);
//					i.putExtra("pk", "");
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
				}
			}
			catch (JSONException e) {e.printStackTrace();}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(Detail_Histori.this, arrayList,R.layout.desain_list, new String[] { TAG_id_detail,TAG_atas, TAG_bawah,},new int[] { R.id.kode_k, R.id.txtNamalkp ,R.id.txtDeskripsilkp});
					setListAdapter(adapter);
					txttotal.setText(total);
					total2=tot;
				}
			});}
	}

	class saveBk extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Detail_Histori.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String ljam_datang=txtjam_datang.getText().toString();
			String ljumlah=txtjam_datang.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_restaurant", id_restaurant));
			params.add(new BasicNameValuePair("id_customer", id_customer));
			params.add(new BasicNameValuePair("jam_datang", ljam_datang));
			params.add(new BasicNameValuePair("jumlah", ljumlah));
			String url=ip+"reservasi/reservasi_update.php";
			Log.v("add",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				sukses= json.getInt(TAG_SUKSES);
				id_reservasi= json.getString("id_reservasi");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {
				final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Detail_Histori.this);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("Registered", true);
				editor.putString("id_reservasi", id_reservasi);
				editor.apply();
				sukses("Simpan");
			} else { gagal("Simpan");}
		}
	}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void sukses(String item){
		new android.app.AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
						finish();
					}}).show();
	}
	public void gagal(String item){
		new android.app.AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:         
        	Intent i = new Intent(getApplicationContext(), DetailMenu.class);
			i.putExtra("pk", "");
			startActivityForResult(i, 100);
            return true;
        }
        return false;
    }

public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		finish();
		return true;
		}
		return super.onKeyDown(keyCode, event);
		}



    public void lengkapi(String item){
        new AlertDialog.Builder(this)
                .setTitle("Lengkapi Data")
                .setMessage("Silakan lengkapi data "+item +" !")
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                        //finish();
                    }}).show();
    }

}
