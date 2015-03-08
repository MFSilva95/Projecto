package com.jadg.mydiabetes;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jadg.mydiabetes.sync.transfer.FileInfo;
import com.jadg.mydiabetes.sync.transfer.Stream;
import com.jadg.mydiabetes.sync.transfer.Stream.StreamBinder;
import com.jadg.mydiabetes.sync.transfer.Transmission;

public class TransferActivity<messenger1> extends Activity {
	private String host = "";
	private int port = 5444;
	private byte[] key;
	private byte[] iv;
	private Intent starter;
	private FileInfo fi;
	private Boolean onPC = null;
	private Hashtable<String, FileInfo> htFileInfo = new Hashtable<String, FileInfo>();

	// Bound Service
	private Stream mService;
	private boolean mBound = false;

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (message.arg1 == RESULT_OK) {
				Toast.makeText(TransferActivity.this,
						"Liga��o estabelecida", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(TransferActivity.this, "Liga��o falhou",
						Toast.LENGTH_LONG).show();
			}
		};
	};

	private Handler handler2 = new Handler() {
		public void handleMessage(Message message) {
			if (message.arg1 == RESULT_OK) {
				Toast.makeText(TransferActivity.this, "Transfer�ncia conclu�da",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getApplicationContext(), ImportExport.class);
	        	
	        	 startActivity(intent);
			} else {
				Toast.makeText(TransferActivity.this, "Transfer�ncia falhou",
						Toast.LENGTH_LONG).show();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);

		
		Toast.makeText(TransferActivity.this, "Aten��o! Esperar liga��o",
				Toast.LENGTH_LONG).show();
		starter = getIntent();
		Bundle extras = starter.getExtras();
		host = extras.getString("host");
		key = extras.getByteArray("key").clone();
		iv = extras.getByteArray("iv").clone();
		onPC = extras.getBoolean("onPC");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		syncFileStructure(null);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	

	/**
	 * Button transferir fetches the DB_Diabetes from the data directory
	 * an gets the images selected
	 * @param view
	 */
	public void transfer(View view) {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler2);
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", port);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", Transmission.PUT_CONTENTS);
		ArrayList<FileInfo> alfi = getSelectFiles((LinearLayout) findViewById(R.id.llFiles));
		FileInfo fi = new FileInfo(getApplicationInfo().dataDir + "/databases",
				"/DB_Diabetes");
		alfi.add(fi);
		intent.putExtra("files", alfi);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		//Messenger messenger1 = new Messenger(handler3);
		
		
	}


	
	

	private ArrayList<FileInfo> getSelectFiles(LinearLayout ll) {
		ArrayList<FileInfo> fil = new ArrayList<FileInfo>();
		LinearLayout ll2;
		CheckBox cb;
		TextView tv;
		FileInfo fi;
		for (int i = 0; i < ll.getChildCount(); i++) {
			ll2 = (LinearLayout) ll.getChildAt(i);
			if (ll2.getChildAt(0) instanceof LinearLayout) {
				fil.addAll(getSelectFiles(ll2));
			} else {
				cb = (CheckBox) ll2.getChildAt(0);
				if (cb == null)
					System.out.println("CheckBox is null");
				else if (cb.isChecked()) {
					tv = (TextView) ll2.getChildAt(1);
					fi = htFileInfo.get(tv.getText().toString());
					if (fi != null && !fi.isDir() && fi.checkFileExists()) {
						fil.add(fi);
					}
				}
			}
		}
		return fil;
	}
	

	/**
	 * Button refresh, call method addFiles to put images from FileInfo in ll
	 * @param view
	 */
	public void button2Click(View view) {
		if (mBound) {
			if ((fi = mService.getFIServer()) == null) {
				System.out.println("Ainda n�o obteve fi do Servidor");
			} else {
				LinearLayout ll = (LinearLayout) findViewById(R.id.llFiles);
				if (ll != null && ll.getChildCount() > 0) {
					try {
						System.out.println("Entrou if button2Click --->"+ll.getChildCount());
						ll.removeViews(0, ll.getChildCount());
						htFileInfo.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("FI antes do addFiles -->"+mService.getFIServer().getAbsolutePath());
				addFiles(ll, mService.getFIServer());

			}
			unbindService(mConnection);
			mBound = false;
		} else {
			System.out.println("Servi�o n�o ligado");
		}

	}

	
	/**
	 * Usado por button2click
	 * @param ll
	 * @param fi
	 */
	public void addFiles(LinearLayout ll, FileInfo fi) {
		LinearLayout ll2;
		CheckBox cb;
		TextView text;
		ImageView pcImage;
		ImageView androidImage;
		String fileName;
		
		FileInfo fi2 = new FileInfo(fi.getRelativePath());
		fi2.update();
		if (fi2.exists() && fi2.getFileList() != null) {
			addFiles2(ll, fi2);
		}
	}

	public void addFiles2(LinearLayout ll, FileInfo fi) {
		LinearLayout ll2;
		CheckBox cb;
		TextView text;
		ImageView androidImage;
		String fileName;

		for (FileInfo fiChild : fi.getFileList()) {

			if (!htFileInfo.containsKey(fiChild.getName())) {
				
				if (fiChild.getName().contains(".jpg")){
				ll2 = new LinearLayout(this);

				cb = new CheckBox(this);
				ll2.addView(cb);

				text = new TextView(this);
				System.out.println("Nome do ficheiro --->"+fiChild.getName());
				fileName = fiChild.getName();
				text.setText(fileName);
				htFileInfo.put(fileName, fiChild);
				ll2.addView(text);

				androidImage = new ImageView(this);
				androidImage.setImageResource(R.drawable.android_icon);
				androidImage.setMaxWidth(50);
				androidImage.setAdjustViewBounds(true);
				ll2.addView(androidImage);
				ll.addView(ll2);
				}
			}

			if (fiChild.isDir()) {
				ll2 = new LinearLayout(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(30, 0, 0, 0);
				ll2.setLayoutParams(lp);

				addFiles2(ll2, fiChild);
				ll.addView(ll2);
			}
		}
	}

	public void selectAll(View view) {
		selectAll2((LinearLayout) findViewById(R.id.llFiles));
	}

	public void selectAll2(LinearLayout ll) {
		LinearLayout ll2;
		try {

			for (int i = 0; i < ll.getChildCount(); i++) {
				ll2 = (LinearLayout) ll.getChildAt(i);
				if (ll2.getChildAt(0) instanceof LinearLayout) {
					selectAll2(ll2);
				} else if (ll2.getChildAt(0) instanceof CheckBox) {
					((CheckBox) ll2.getChildAt(0)).setChecked(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void syncServer() {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		// TODO PASSAR esta funcao para SyncServerActivity
		byte b = 15;
		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler2);
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", 5445);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", b);
		intent.putExtra("file", Environment.getExternalStorageDirectory()
				+ "/MyDiabetes");
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void syncFileStructure(View view) {
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		Intent intent = new Intent(this, Stream.class);
		Messenger messenger = new Messenger(handler);
		intent.putExtra("MESSENGER", messenger);
		intent.putExtra("host", host);
		intent.putExtra("port", port);
		intent.putExtra("key", key);
		intent.putExtra("iv", iv);
		intent.putExtra("cmd", Transmission.GET_INFO);
		intent.putExtra("file", Environment.getExternalStorageDirectory()
				+ "/MyDiabetes");
//		if (mBound) {
//			unbindService(mConnection);
//			mBound = false;
//		}
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			StreamBinder binder = (StreamBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
}
