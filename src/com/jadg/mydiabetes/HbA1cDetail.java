package com.jadg.mydiabetes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.database.DB_Write;
import com.jadg.mydiabetes.database.HbA1cDataBinding;
import com.jadg.mydiabetes.database.NoteDataBinding;
import com.jadg.mydiabetes.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.dialogs.TimePickerFragment;

import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class HbA1cDetail extends Activity {

	int idHbA1c = 0;
	int idNote = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hba1c_detail);
		// Show the Up button in the action bar.
		getActionBar();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idHbA1c = Integer.parseInt(id);
			HbA1cDataBinding toFill = rdb.HbA1c_GetById(Integer.parseInt(id));
			
			EditText value = (EditText)findViewById(R.id.et_HbA1cDetail_Value);
			value.setText(String.valueOf(toFill.getValue()));
			EditText data = (EditText)findViewById(R.id.et_HbA1cDetail_Data);
			data.setText(toFill.getDate());
			EditText hora = (EditText)findViewById(R.id.et_HbA1cDetail_Hora);
			hora.setText(toFill.getTime());
			EditText note = (EditText)findViewById(R.id.et_HbA1cDetail_Notes);
			if(toFill.getIdNote()!=-1){
				NoteDataBinding n = new NoteDataBinding();
				n=rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote=n.getId();
			}
		rdb.close();
		}else{
			FillDateHour();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		
		Bundle args = getIntent().getExtras();
		if(args!=null){
			inflater.inflate(R.menu.hba1c_detail_edit, menu);
		}else{
			inflater.inflate(R.menu.hba1c_detail, menu);
		}
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_HbA1cDetail_Save:
				AddHbA1cRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_HbA1cDetail_Delete:
				DeleteHbA1cRead();
				return true;
			case R.id.menuItem_HbA1cDetail_EditSave:
				UpdateHbA1cRead();
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("SimpleDateFormat")
	public void FillDateHour(){
		EditText date = (EditText)findViewById(R.id.et_HbA1cDetail_Data);
		final Calendar c = Calendar.getInstance();
	    Date newDate = c.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    String dateString = formatter.format(newDate);
        date.setText(dateString);
        
        EditText hour = (EditText)findViewById(R.id.et_HbA1cDetail_Hora);
        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(newDate);
        hour.setText(timeString);
	}
	
	@SuppressWarnings("deprecation")
	public void showDatePickerDialog(View v){
		DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_HbA1cDetail_Data);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "DatePicker");
	}
	
	@SuppressWarnings("deprecation")
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle args = new Bundle();
	    args.putInt("textbox",R.id.et_HbA1cDetail_Hora);
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "timePicker");
	    
	}
	
	public void AddHbA1cRead(){
		EditText value = (EditText)findViewById(R.id.et_HbA1cDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_HbA1cDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_HbA1cDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_HbA1cDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		HbA1cDataBinding hba1c = new HbA1cDataBinding();
		
		if(!note.getText().toString().equals("")){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			hba1c.setIdNote(wdb.Note_Add(n));
		}
		
		
		hba1c.setIdUser(idUser);
		hba1c.setValue(Double.parseDouble(value.getText().toString()));
		hba1c.setDate(data.getText().toString());
		hba1c.setTime(hora.getText().toString());
		
		wdb.HbA1c_Save(hba1c);
		
		wdb.close();
		rdb.close();
		
		
	}

	public void UpdateHbA1cRead(){
		EditText value = (EditText)findViewById(R.id.et_HbA1cDetail_Value);
		EditText data = (EditText)findViewById(R.id.et_HbA1cDetail_Data);
		EditText hora = (EditText)findViewById(R.id.et_HbA1cDetail_Hora);
		EditText note = (EditText)findViewById(R.id.et_HbA1cDetail_Notes);
		
		
		DB_Write wdb = new DB_Write(this);
		
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());
		
		HbA1cDataBinding hba1c = new HbA1cDataBinding();
		
		if(!note.getText().toString().equals("") && idNote==0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			hba1c.setIdNote(wdb.Note_Add(n));
		}
		if(idNote!=0){
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}
		
		hba1c.setId(idHbA1c);
		hba1c.setIdUser(idUser);
		hba1c.setValue(Double.parseDouble(value.getText().toString()));
		hba1c.setDate(data.getText().toString());
		hba1c.setTime(hora.getText().toString());
		
		wdb.HbA1c_Update(hba1c);
		
		wdb.close();
		rdb.close();
	}
	
	public void DeleteHbA1cRead(){
		final Context c = this;
		new AlertDialog.Builder(this)
	    .setTitle("Eliminar leitura?")
	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	             //Falta verificar se n�o est� associada a nenhuma entrada da DB
	        	 //Rever porque n�o elimina o registo de glicemia
	        	 DB_Write wdb = new DB_Write(c);
	        	 try {
	        		 wdb.HbA1c_Delete(idHbA1c);
	        		 goUp();
	        	 }catch (Exception e) {
	        		 Toast.makeText(c, "N�o pode eliminar esta leitura!", Toast.LENGTH_LONG).show();
	     		 }
	             wdb.close();
	             
	         }
	    })
	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int whichButton) {
	                // Do nothing.
	         }
	    }).show();
	}
	
	public void goUp(){
		NavUtils.navigateUpFromSameTask(this);
	}

}
