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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddMenu extends Activity {

	private GridView gridView;
	private GridViewAdapter gridAdapter;

	int jd=0;

	String[]arid_menu;
	String[]arnama_menu;
	String[]ardeskripsi;
	String[]arkategori;
	String[]arharga;
	String[]arstatus;
	String[]argambar;
	Bitmap[]arBitmap;



	String ip="",myPosisi,myLati,myLongi;
	String id_customer,nama_customer,id_reservasi,id_restaurant;
	String id;
	String id0="";

	EditText txtjumlah;
	EditText txtcatatan;

	Button btnProses;
	Button btnHapus;

	int sukses;
	int jb=0;

	Spinner spinmenu;
	String[]pilBar;
	String[]pilKode;
	TextView txtmenu;

	JSONArray myJSON = null;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_id_reservasi = "id_reservasi";
	private static final String TAG_id_menu = "id_menu";
	private static final String TAG_jumlah = "jumlah";
	private static final String TAG_catatan = "catatan";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AddMenu.this);
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
		id_restaurant = i.getStringExtra("id_restaurant");
		myLati = i.getStringExtra("myLati");
		myLongi = i.getStringExtra("myLongi");

//oncreate
		txtmenu=(TextView)findViewById(R.id.txtmenu);
		spinmenu = (Spinner) findViewById(R.id.spinmenu);
		spinmenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				txtmenu.setText(pilKode[position]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		new getmenu().execute();

		txtjumlah= (EditText) findViewById(R.id.txtjumlah);
		txtcatatan= (EditText) findViewById(R.id.txtcatatan);




		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id0 = i.getStringExtra("pk");
		id=id0;
		if(id0.length()>0){
			new get().execute();
			btnProses.setText("Update Data");
			btnHapus.setVisibility(View.VISIBLE);
			btnProses.setVisibility(View.GONE);
		}
		else{
			btnProses.setText("Add New Data");
			btnHapus.setVisibility(View.GONE);
		}

		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String ljumlah= txtjumlah.getText().toString();
				String lcatatan= txtcatatan.getText().toString();

				 if(ljumlah.length()<1){lengkapi("jumlah");}
					else{
					if(id0.length()>0){
						new update().execute();
					}
					else{
						new save().execute();
					}
				}//else

			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new del().execute();
			}});
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			pDialog = new ProgressDialog(AddMenu.this);
//			pDialog.setMessage("Load data detail. Please Wait...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(true);
//			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_detail", id0));

				String url=ip+"detail/detail_detail.php";
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
								txtjumlah.setText(myJSON.getString(TAG_jumlah));
								txtcatatan.setText(myJSON.getString(TAG_catatan));
								String db=myJSON.getString("id_menu");
								int index=0;
								for(int j=0;j<jb;j++){
									if(pilKode[j].equalsIgnoreCase(db)){
										index=j;break;
									}
								}//for
								spinmenu.setSelection(index);




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
		protected void onPostExecute(String file_url) {
			//pDialog.dismiss();
			}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class save extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddMenu.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lid_menu= txtmenu.getText().toString();
			String ljumlah= txtjumlah.getText().toString();
			String lcatatan= txtcatatan.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_restaurant", id_restaurant));
			params.add(new BasicNameValuePair("id_customer", id_customer));

			params.add(new BasicNameValuePair("id_menu", lid_menu));
			params.add(new BasicNameValuePair("jumlah", ljumlah));
			params.add(new BasicNameValuePair("catatan", lcatatan));
			String url=ip+"detail/detail_add.php";
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
				final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AddMenu.this);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("Registered", true);
				editor.putString("id_reservasi", id_reservasi);
				editor.apply();
				sukses("Simpan");
			} else { gagal("Simpan");}
		}
	}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class update extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddMenu.this);
			pDialog.setMessage("Mengubah data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lid_menu= txtmenu.getText().toString();
			String ljumlah= txtjumlah.getText().toString();
			String lcatatan= txtcatatan.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_detail", id0));
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_menu", lid_menu));
			params.add(new BasicNameValuePair("jumlah", ljumlah));
			params.add(new BasicNameValuePair("catatan", lcatatan));
	
			String url=ip+"detail/detail_update.php";
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
			pDialog = new ProgressDialog(AddMenu.this);
			pDialog.setMessage("Menghapus data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_detail", id0));

				String url=ip+"detail/detail_del.php";
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
						Intent i = new Intent(AddMenu.this, Detail_Reservasi.class);
						i.putExtra("pk", id_restaurant);
						i.putExtra("myLati", myLati);
						i.putExtra("myLongi", myLongi);
						startActivity(i);
						finish();
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
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
	int sukses1=0;
	//FunctionGetValue
	class getmenu extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_restaurant",id_restaurant));
			JSONObject json = jsonParser.makeHttpRequest(ip + "menu/menu_show.php", "GET", params);
			Log.d("show: ", json.toString());
			try {
				 sukses1 = json.getInt(TAG_SUKSES);
				if (sukses1 == 1) {
					myJSON = json.getJSONArray(TAG_record);
					jb=myJSON.length();
					pilBar=new String[jb];
					pilKode=new String[jb];
					for (int i = 0; i < jb; i++) {
						JSONObject c = myJSON.getJSONObject(i);
						String id_menu = c.getString("id_menu");
						String nama_menu= c.getString("nama_menu");
						String harga= c.getString("harga");
						String kategori= c.getString("kategori");
						pilBar[i]=nama_menu+" "+harga +" ("+kategori+")";
						pilKode[i]=id_menu;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {
			if(sukses1>0) {
				runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddMenu.this, android.R.layout.simple_spinner_item, pilBar);
						spinmenu.setAdapter(adapter);

					}
				});
			}

			new load().execute();
		}
	}

	private ArrayList<ImageItem> getData() {
		final ArrayList<ImageItem> imageItems = new ArrayList<>();
		//TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
		for (int i = 0; i < jd; i++) {//jd/imgs.length()
			//Bitmap bitmap = BitmapFactory.decodeResource(getResources(),arBitmap[i]);// imgs.getResourceId(i, -1)
			imageItems.add(new ImageItem(arBitmap[i], arnama_menu[i],arkategori[i],arharga[i],0.0,"",0));
		}
		return imageItems;
	}

	class load extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddMenu.this);
			pDialog.setMessage("Load data. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_restaurant",id_restaurant));
			JSONObject json = jsonParser.makeHttpRequest(ip+"menu/menu_show.php", "GET", params);
			// Log.d("show: ", json.toString());
			try {
				int sukses = json.getInt(TAG_SUKSES);
				if (sukses == 1) {
					myJSON = json.getJSONArray(TAG_record);
					jd=myJSON.length();
					arid_menu=new String[jd];
					arnama_menu=new String[jd];
					ardeskripsi=new String[jd];
					arkategori=new String[jd];
					arharga=new String[jd];
					arstatus=new String[jd];
					argambar=new String[jd];
					arBitmap=new Bitmap[jd];

					for (int i = 0; i < myJSON.length(); i++) {
						JSONObject c = myJSON.getJSONObject(i);
						arid_menu[i]= c.getString("id_menu");
						arnama_menu[i]= c.getString("nama_menu");
						ardeskripsi[i]= c.getString("deskripsi");
						arkategori[i]= c.getString("kategori");
						arharga[i]= c.getString("bawah");
						arstatus[i]= c.getString("status");
						argambar[i]= c.getString("gambar");
						String gb=ip+"ypathfile/"+ argambar[i];
						Log.v("URL",gb);
						arBitmap[i]=getBitmapFromURL(gb);
					}
				}
			}
			catch (JSONException e) {e.printStackTrace();}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					gridView = (GridView) findViewById(R.id.gridView);
					gridAdapter = new GridViewAdapter(AddMenu.this, R.layout.desain_menu, getData());
					gridView.setAdapter(gridAdapter);

//					gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//						public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//							ImageItem item = (ImageItem) parent.getItemAtPosition(position);
//							Intent intent = new Intent(AddMenu.this, Detail_restaurant.class);
//							intent.putExtra("pk", arid_restaurant[position]);//item.getTitle()
//							intent.putExtra("myLati", myLati);
//							intent.putExtra("myLongi", myLongi);
////                            intent.putExtra("title2", ardeskripsi[position]);
////                            intent.putExtra("title3", aremail[position]);
////                            intent.putExtra("title4", arstatus[position]);
////                            intent.putExtra("gambar", argambar[position]);
//
//							//intent.putExtra("image", item.getImage());
//							startActivity(intent);
				//		}
				//	});
				}
			});}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}


}
