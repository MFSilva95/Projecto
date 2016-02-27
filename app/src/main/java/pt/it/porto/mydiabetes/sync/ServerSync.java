package pt.it.porto.mydiabetes.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.PhotoSyncDb;
import pt.it.porto.mydiabetes.database.Preferences;

public class ServerSync {

	private static final String BASE_URL = "https://mydiabetes.dcc.fc.up.pt/newsite/";
	private static ServerSync instance;

	public static final MediaType MEDIA_TYPE_BINARY = MediaType.parse("application/octet-stream");
	private String username;
	private String password;
	private PhotoSyncDb photoSyncDb;
	private OkHttpClient client;

	private Context context;
	private ServerSyncListener listener;
	private Handler mainHandler;

	private ServerSync() {
	}

	public static ServerSync getInstance(Context context) {
		if (instance == null) {
			synchronized (ServerSync.class) {
				if (instance == null) {
					instance = new ServerSync();
				}
			}
		}
		instance.setContext(context);
		return instance;
	}

	public void send(final ServerSyncListener listener) {
		this.listener = listener;
		client = new OkHttpClient();
		File file = new File(Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/DB_Diabetes");

		username = Preferences.getUsername(context);
		password = Preferences.getPassword(context);

		RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("user", username)
				.addFormDataPart("password", password)
				.addFormDataPart("db", "db", RequestBody.create(MEDIA_TYPE_BINARY, file))
				.build();


		Request request = new Request.Builder().url(BASE_URL + "transfer_db.php")
				.post(formBody)
				.build();


		mainHandler = new Handler(context.getMainLooper());
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				ServerSync.this.onFailure();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// now sends the images
				photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(context));
				processNextPhoto();
			}
		});

	}

	//
	private void sendPhoto(final String photo) {
		File file = new File(photo);
		RequestBody formBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("user", username)
				.addFormDataPart("password", password)
				.addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_BINARY, file))
				.build();


		Request request = new Request.Builder().url(BASE_URL + "transfer_img.php")
				.post(formBody)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				ServerSync.this.onFailure();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				photoSyncDb.removePhoto(photo);
				processNextPhoto();
			}
		});
	}

	private void onFailure() {
		if (listener != null) {
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					listener.onSyncUnSuccessful();
				}
			});
		}
	}

	private void processNextPhoto() {
		Cursor listPhotos = photoSyncDb.getListPhotos();
		if (listPhotos.getCount() > 0) {
			listPhotos.moveToFirst();
			String photo=listPhotos.getString(0);
			if(!new File(photo).exists()){
				photoSyncDb.removePhoto(photo);
				processNextPhoto();
			} else {
				sendPhoto(listPhotos.getString(0));
			}
		} else {
			if (listener != null) {
				mainHandler.post(new Runnable() {
					@Override
					public void run() {
						listener.onSyncSuccessful();
					}
				});
			}
		}
	}

	private void setContext(Context context) {
		this.context = context;
	}

	public interface ServerSyncListener {
		void onSyncSuccessful();

		void onSyncUnSuccessful();

	}

}
