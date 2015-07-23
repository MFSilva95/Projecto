package com.jadg.mydiabetes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.jadg.mydiabetes.database.CarbsDataBinding;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.database.TagDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;
import com.jadg.mydiabetes.usability.ActivityEvent;


public class CarboHydrateDetail extends Activity {

	//photo variables - start
	final private int CAPTURE_IMAGE = 2;
	Uri imgUri;
	Bitmap b;
	//photo variables - end
	int idNote = 0;
	int id_ch = -1;

	// variavel que contem o nome da janela em que vai ser contado o tempo
	// contem o tempo de inicio ou abertura dessa janela
	// no fim de fechar a janela vai conter o tempo em que a janela foi fechada
	// e vai criar uma entrada na base de dados a registar os tempos
	ActivityEvent activityEvent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carbohydrate_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillTagSpinner();
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);

		// bloco de codigo que verifica se o utilizador carregou numa zona
		// "vazia" do ecra. Neste caso regista o click como um missed click
		ScrollView sv = (ScrollView)findViewById(R.id.carboHydrateDetailScrollView);
		sv.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DB_Write write = new DB_Write(CarboHydrateDetail.this);    // gera uma nova instancia de escrita na base de dados
					write.newClick("CarboHydrateDetail_Missed_Click");                // regista o clique na base de dados

					write.newMissed(event.getX(), event.getY(), "CarboHydrateDetail");
					Log.d("test", event.toString());
				}
				return true;
			}
		});

		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			id_ch = Integer.parseInt(args.getString("Id"));
			CarbsDataBinding toFill = rdb.CarboHydrate_GetById(Integer.parseInt(id));

			Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getId_Tag()).getName());
			EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
			carbs.setText(toFill.getCarbsValue().toString());
			EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
			data.setText(toFill.getDate());
			Log.d("data reg carb", toFill.getDate());
			hora.setText(toFill.getTime());

			EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);
			if(toFill.getId_Note()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getId_Note());
				note.setText(n.getNote());
				idNote=n.getId();
			}

			EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
			if(!toFill.getPhotoPath().equals("")){
				photopath.setText(toFill.getPhotoPath());
				Log.d("foto path", "foto: " + toFill.getPhotoPath());
				ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = (int)(displaymetrics.heightPixels * 0.1);
				int width = (int)(displaymetrics.widthPixels * 0.1);
				b = decodeSampledBitmapFromPath(toFill.getPhotoPath(),width,height );

				img.setImageBitmap(b);

			}

			Log.d("photopath", toFill.getPhotoPath());

			rdb.close();
		}else{
			FillDateHour();
			SetTagByTime();
			hora.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					SetTagByTime(); }
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
				@Override
				public void afterTextChanged(Editable s) { }
			});
		}



	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.carbo_hydrate_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.carbo_hydrate_detail, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bundle args = getIntent().getExtras();
		DB_Write write = new DB_Write(this);							// gera uma nova instancia de escrita na base de dados

		switch (item.getItemId()) {
			case android.R.id.home:
				write.newClick("menuItem_CarboHydrateDetail_Home");	// regista o clique na base de dados

				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Save:
				write.newClick("menuItem_CarboHydrateDetail_Save");		// regista o clique na base de dados

				AddCarbsRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Delete:
				write.newClick("menuItem_CarboHydrateDetail_Delete");	// regista o clique na base de dados

				DeleteCarbsRead(Integer.parseInt(args.getString("Id")));
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_EditSave:
				write.newClick("menuItem_CarboHydrateDetail_EditSave");	// regista o clique na base de dados

				UpdateCarbsRead(Integer.parseInt(args.getString("Id")));
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Esta funcao e chamada sempre que a actividade atual passa a ser a CarboHydrateDetail
	// ou seja, quando a janela a mostrar é a janela da CarboHydrateDetail. Assim, é nesta
	// funcao que o timer inicia.
	@Override
	public void onResume(){
		activityEvent = new ActivityEvent(new DB_Write(this), "CarboHydrateDetail");
		super.onPause();
	}

	// Esta funcao e chamada sempre que a actividade atual deixa de ser a CarboHydrateDetail
	// ou seja, quando a janela a mostrar deixa de ser a janela da CarboHydrateDetail. Assim,
	// é nesta funcao que o timer para e que guardamos a nova entrada na base de dados.
	@Override
	public void onPause(){
		activityEvent.stop();
		super.onPause();
	}

	public void SetTagByTime(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		final Calendar c = Calendar.getInstance();
		Date newDate = c.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(newDate);
		date.setText(dateString);

		EditText hour = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		formatter = new SimpleDateFormat("HH:mm:ss");
		String timeString = formatter.format(newDate);
		hour.setText(timeString);
	}
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox",R.id.et_CarboHydrateDetail_Data);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox",R.id.et_CarboHydrateDetail_Hora);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "timePicker");

	}

	public void FillTagSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
		ArrayList<String> allTags = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();


		if(t!=null){
			for (TagDataBinding i : t){
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void AddCarbsRead(){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if(carbs.getText().toString().equals("")){
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsDataBinding carb = new CarbsDataBinding();

		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}


		carb.setId_User(idUser);
		carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
		carb.setId_Tag(idTag);
		carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(data.getText().toString());
		carb.setTime(hora.getText().toString());



		reg.Carbs_Save(carb);
		reg.close();
		goUp();
	}


	//PHOTO - START

	public Uri setImageUri() {
		// Store image in /MyDiabetes
		File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
		File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
		if(!dir.exists()){
			dir.mkdir();
		}
		imgUri = Uri.fromFile(file);
		return imgUri;
	}



	public void TakePhoto(View v) {
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		if(!photopath.getText().toString().equals("")){
			final Intent intent = new Intent(this, ViewPhoto.class);
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", id_ch);
			intent.putExtras(argsToPhoto);
			startActivityForResult(intent, 101010);
		}else{
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
			startActivityForResult(intent, CAPTURE_IMAGE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);
		if (resultCode != Activity.RESULT_CANCELED) {
			if (requestCode == CAPTURE_IMAGE) {
				Toast.makeText(getApplicationContext(), getString(R.string.photoSaved) +" " + imgUri.getPath(), Toast.LENGTH_LONG).show();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = (int)(displaymetrics.heightPixels * 0.1);
				int width = (int)(displaymetrics.widthPixels * 0.1);
				b = decodeSampledBitmapFromPath(imgUri.getPath(),width,height );

				img.setImageBitmap(b);
				photopath.setText(imgUri.getPath());

			}else if (requestCode == 101010){
				Log.d("Result:", resultCode+"");
				//se tivermos apagado a foto dá result code -1
				//se voltarmos por um return por exemplo o resultcode é 0
				if(resultCode==-1){
					photopath.setText("");
					img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
				}
			}else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}

	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (imgUri != null) {
			outState.putString("cameraImageUri", imgUri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			imgUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
			EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
			ImageView img = (ImageView)findViewById(R.id.iv_CarboHydrateDetail_Photo);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int)(displaymetrics.heightPixels * 0.1);
			int width = (int)(displaymetrics.widthPixels * 0.1);
			b = decodeSampledBitmapFromPath(imgUri.getPath(),width,height );

			img.setImageBitmap(b);
			photopath.setText(imgUri.getPath());
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}


	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return adjustImageOrientation(BitmapFactory.decodeFile(path, options),path);
	}


	private static Bitmap adjustImageOrientation(Bitmap image, String picturePath ) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(picturePath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int rotate = 0;
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
			}

			if (rotate != 0) {
				int w = image.getWidth();
				int h = image.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap & convert to ARGB_8888, required by tess
				image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

			}
		} catch (IOException e) {
			return null;
		}
		return image.copy(Bitmap.Config.ARGB_8888, true);
	}


	//PHOTO - END






	public static void SelectSpinnerItemByValue(Spinner spnr, String value)
	{
		SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++)
		{
			if(adapter.getItem(position).equals(value))
			{
				spnr.setSelection(position);
				return;
			}
		}
	}

	public void DeleteCarbsRead(final int id){
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle("Eliminar leitura?")
				.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Carbs_Delete(id);
							goUp();
						}catch (Exception e) {
							Toast.makeText(c, "Não pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}

	public void UpdateCarbsRead(int id){
		Spinner tagSpinner = (Spinner)findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText)findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText)findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText)findViewById(R.id.et_CarboHydrateDetail_Notes);

		if(carbs.getText().toString().equals("")){
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsDataBinding carb = new CarbsDataBinding();

		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}

		carb.setId(id);
		carb.setId_User(idUser);
		carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
		carb.setId_Tag(idTag);
		carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(data.getText().toString());
		carb.setTime(hora.getText().toString());


		reg.Carbs_Update(carb);
		reg.close();
		goUp();
	}
}
