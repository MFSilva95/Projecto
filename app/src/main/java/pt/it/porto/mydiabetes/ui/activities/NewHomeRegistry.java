package pt.it.porto.mydiabetes.ui.activities;

/**
 * Created by Diogo on 22/02/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;
import pt.it.porto.mydiabetes.ui.views.ExtendedEditText;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class NewHomeRegistry extends AppCompatActivity implements InsulinCalcFragment.CalcListener {

    @Override
    public void setup() {
        fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
        showCalcs();
    }

    private enum RegistryFields{CARBS,INSULIN,GLICEMIA,PLUS};
    private LinearLayout bottomSheetViewgroup;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout contentLayout;
    private ArrayList<RegistryFields> buttons;
    private Boolean insulinManual = false;
    private Boolean getIsManual(){
        return insulinManual;
    }
    private void setIsManual(boolean b){
        insulinManual = b;
        Log.i(TAG, "setIsManual: ->"+b);
    }

    private Calendar registerDate;
    private TextView registerDateTextV;
    private TextView registerTimeTextV;
    protected InsulinCalcFragment fragmentInsulinCalcsFragment;
    protected InsulinCalculator insulinCalculator = null;
    private boolean useIOB = true;
    private boolean expandInsulinCalcsAuto = false;
    private String TAG = "newREG";
    public final static int IMAGE_CAPTURE = 2;
    public final static int IMAGE_VIEW = 3;
    private static final String CALCS_OPEN = "calcs open";
    private static final String GENERATED_IMAGE_URI = "generated_image_uri";
    private Uri generatedImageUri;
    private Uri imgUri;
    private Bitmap b;
    private boolean isUpdate =false;
    private int noteId;

    private String date = "";
    private String time = "";


    @Nullable
    private GlycemiaRec glycemiaData;
    @Nullable
    private CarbsRec carbsData;
    @Nullable
    private InsulinRec insulinData;

    public static final String ARG_CARBS = "ARG_CARBS";
    public static final String ARG_INSULIN = "ARG_INSULIN";
    public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        Bundle args = getIntent().getExtras();
        if (args != null) {
            inflater.inflate(R.menu.weight_detail_delete, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_WeightDetail_Delete:
                deleteRegister();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void finishAfterTransition() {
        contentLayout.setAlpha(0);
        super.finishAfterTransition();
    }
    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED && requestCode == IMAGE_CAPTURE) {
            setImgURI(generatedImageUri);
        } else if (requestCode == IMAGE_VIEW) {
            //se tivermos apagado a foto dá result code -1
            //se voltarmos por um return por exemplo o resultcode é 0
            if (resultCode == -1) {
                imageRemoved();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);
        outState.putBoolean(CALCS_OPEN, isFragmentShowing());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
            generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
        }
        if (savedInstanceState != null && savedInstanceState.getBoolean(CALCS_OPEN, false)) {
            ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
            if (calcInsulinInfo != null) {
                calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_grey_900_24dp));
            }
            fragmentInsulinCalcsFragment = new InsulinCalcFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_calcs, fragmentInsulinCalcsFragment).commit();
            getFragmentManager().executePendingTransactions();
            this.fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
            showCalcs();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        contentLayout = (LinearLayout) findViewById(R.id.content_panel);
        insulinCalculator = new InsulinCalculator(this);
        FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));
        useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        imgUri = null;
        buttons = new ArrayList<>();
        registerDate = Calendar.getInstance();

        insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
        //String currentTime = android.text.format.DateFormat.getTimeFormat(this.getApplicationContext()).format(new java.util.Date());

        bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        bottomSheetBehavior.setHideable(true);


        registerDateTextV = (TextView) findViewById(R.id.registryDate);
        registerTimeTextV = (TextView) findViewById(R.id.registerTime);
        buttons.add(RegistryFields.PLUS);
        showBottomSheet();

        registerDateTextV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showDatePickerDialog(view);
            }
		});
        registerTimeTextV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        (findViewById(R.id.bt_add_more_content)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showBottomSheet();
            }
        });
        ((Spinner) findViewById(R.id.tag_spinner)).setAdapter(new StringSpinnerAdapter(this, getResources().getStringArray(R.array.daytimes)));
        setupBottomSheet();
        Bundle args = getIntent().getExtras();
        if(args != null){
            fillParameters(args);
        }else{
            Calendar time = Calendar.getInstance();
            setDate(time);
            setTime(time);
        }
    }
    private void deleteRegister(){
        final Context c = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleteReading))
                .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Falta verificar se não está associada a nenhuma entrada da DB
                        //Rever porque não elimina o registo de glicemia
                        DB_Write reg = new DB_Write(c);
                        try {
                            //Weight_Delete();
                            if(glycemiaData!=null){reg.Glycemia_Delete(glycemiaData.getId());}
                            if(carbsData!=null){reg.Carbs_Delete(carbsData.getId());}
                            if(insulinData!=null){reg.Insulin_Delete(insulinData.getId());}
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
                        }
                        reg.close();

                    }
                })
                .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }
    private void showTimePickerDialog(View v) {

        DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.registerTime,
                DateUtils.getTimeCalendar(((TextView) v).getText().toString()));
        ((TimePickerFragment) newFragment).setListener(new TimePickerFragment.TimePickerChangeListener() {
            @Override
            public void onTimeSet(String time) {
                Calendar calendar = DateUtils.getTimeCalendar(time);
                if (calendar != null) {
                    setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),calendar.get(Calendar.MINUTE));
                    String timeString = DateUtils.getFormattedTime(registerDate);
                    registerTimeTextV.setText(timeString);
                }
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(
                R.id.registryDate,
                new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        setDate(year, month, day);
                    }
                }, DateUtils.getDateCalendar(((TextView) v).getText().toString()));
        newFragment.show(getFragmentManager(), "DatePicker");
    }
    private void setDate(Calendar c) {
        registerDate.set(Calendar.YEAR,c.get(Calendar.YEAR));
        registerDate.set(Calendar.MONTH,c.get(Calendar.MONTH));
        registerDate.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
        registerDateTextV.setText(DateUtils.getFormattedDate(registerDate));
    }
    private void setTime(Calendar c){
        registerDate.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        registerDate.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        registerDate.set(Calendar.SECOND, c.get(Calendar.SECOND));
        registerTimeTextV.setText(DateUtils.getFormattedTime(registerDate));
    }
    private void setDate(int year, int month, int day) {
        registerDate.set(Calendar.YEAR,year);
        registerDate.set(Calendar.MONTH,month);
        registerDate.set(Calendar.DAY_OF_MONTH,day);
        registerDateTextV.setText(DateUtils.getFormattedDate(registerDate));
    }
    private void setTime(int hour, int minute, int second){
        registerDate.set(Calendar.HOUR_OF_DAY, hour);
        registerDate.set(Calendar.MINUTE, minute);
        registerDate.set(Calendar.SECOND, second);

        StringBuilder displayTime = new StringBuilder(18);
        displayTime.append(registerDate.get(Calendar.HOUR_OF_DAY));
        displayTime.append(":");
        displayTime.append(registerDate.get(Calendar.MINUTE));
        registerTimeTextV.setText(displayTime);
    }
    private void addContent(int layout) {
        contentLayout.addView(LayoutInflater.from(this).inflate(layout, contentLayout, false), 0);//contentLayout.getChildCount() - 1);
    }
    private void addContentAt(int layout, int pos) {
        contentLayout.addView(LayoutInflater.from(this).inflate(layout, contentLayout, false), pos);//contentLayout.getChildCount() - 1);
    }
    private void removeContent(int child) {
        contentLayout.removeViewAt(child);
    }
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void requestKeyboard(View view) {
        view.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        lp.setAnchorId(R.id.bottom_sheet);
        lp.anchorGravity = Gravity.TOP | GravityCompat.END;
        fab.setLayoutParams(lp);*/

//        for(RegistryFields reg:buttons){
//            switch (reg){
//                case CARBS:bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);continue;
//                case GLICEMIA:bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);continue;
//                case INSULIN:bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);continue;
//            }
//            Log.i(TAG, "showBottomSheet: cenas");
//        }
    }
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    private void save() {
        try{
            validateInfo_Save();
            finish();
        }catch (Exception e){
            return;
        }
    }
    private void validateInfo_Save()throws Exception{
        /*TextView time = (TextView) findViewById(R.id.registerTimeTextV);
        if (registerDate != null && time != null) {

            //SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.iso8601Format.toPattern());//yyyy-MM-dd HH:mm
            //String currDate = registerDate.get(Calendar.YEAR)+"-"+registerDate.get(Calendar.MONTH)+"-"+registerDate.get(Calendar.DAY_OF_MONTH)+" "+time.getText();
        }*/
        for(RegistryFields field:buttons){
            try {
            switch (field){
                    case CARBS:
                        addMealRead();
                        break;
                    case GLICEMIA:
                        addGlycemiaRead();
                        break;
                    case INSULIN:
                        addInsulinRead();
                        break;
                }
            }catch (Exception e){
               throw e;
            }
        }

        BadgeUtils.addLogBadge(getBaseContext());
        BadgeUtils.addDailyBadge(getBaseContext());
        LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "log");
        setResult(Home.CHANGES_OCCURRED, this.getIntent());
    }
    public void addGlycemiaRead() throws Exception{

        Spinner tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        TextInputLayout gliInput = (TextInputLayout) findViewById(R.id.glycemia_txt);
        EditText glycemia = gliInput.getEditText();

        if (glycemia != null && glycemia.getText().toString().equals("")) {
            glycemia.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
            gliInput.setError(getString(R.string.glicInputError));
            throw new Exception("wrong parameter");
        }

        //Get id of user
        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getId();

        //Get id of selected tag
        String tag = null;
        if (tagSpinner != null) {
            tag = tagSpinner.getSelectedItem().toString();
        }
        int idTag = rdb.Tag_GetIdByName(tag);
        rdb.close();

        DB_Write reg = new DB_Write(this);


        if(isUpdate){
            int glicValue;
            try{
                glicValue = Integer.parseInt(glycemia.getText().toString());
            }catch (Exception e){
                gliInput.setError(getString(R.string.glicInputError));
                glycemia.requestFocus();
                throw new Exception("wrong parameter");
            }
            glycemiaData.setValue(glicValue);
            glycemiaData.setDateTime(registerDate);
            glycemiaData.setIdTag(idTag);
            reg.Glycemia_Update(glycemiaData);
        }else{
            GlycemiaRec gly = new GlycemiaRec();

            gly.setIdUser(idUser);
            int glicValue;
            try{
                glicValue = Integer.parseInt(glycemia.getText().toString());
            }catch (Exception e){
                gliInput.setError(getString(R.string.glicInputError));
                glycemia.requestFocus();
                throw new Exception("wrong parameter");
            }
            gly.setValue(glicValue);
            gly.setDateTime(registerDate);
            gly.setIdTag(idTag);
            reg.Glycemia_Save(gly);
        }
        reg.close();
    }
    public void addInsulinRead() throws Exception{

        Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        ExtendedEditText insulinUnits = (ExtendedEditText) findViewById(R.id.insulin_intake);
        TextInputLayout insulinInputLayout = (TextInputLayout) findViewById(R.id.insulin_admin);
        insulinInputLayout.setHintEnabled(true);

        Spinner tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        //tem de ter um target inserido
        DB_Read read = new DB_Read(this);
        if (!read.Target_HasTargets()) {
            read.close();
            //TODO ShowDialogAddTarget();
            return;
        }
        if (insulinUnits.getText().toString().equals("")) {
            insulinUnits.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(insulinUnits, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        //Get id of user
        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getId();

        //Get id of selected tag
        String tag = tagSpinner.getSelectedItem().toString();
        int idTag = rdb.Tag_GetIdByName(tag);

        //Get id of selected insulin
        String insulin = insulinSpinner.getSelectedItem().toString();
        int idInsulin = rdb.Insulin_GetByName(insulin).getId();
        int idGlycemia = 0;
        boolean hasGlycemia = false;
        DB_Write reg = new DB_Write(this);

        if(isUpdate){
            insulinData.setIdInsulin(idInsulin);
            insulinData.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
            insulinData.setDateTime(registerDate);
            float insulinDose;
            try{
                insulinDose = Float.parseFloat(insulinUnits.getText().toString());
            }catch (Exception e){
                insulinInputLayout.setError(getString(R.string.glicInputError));
                insulinInputLayout.getEditText().requestFocus();
                throw new Exception("wrong parameter");
            }
            insulinData.setInsulinUnits(insulinDose);
            reg.Insulin_Update(insulinData);
        }else{
            InsulinRec ins = new InsulinRec();
            ins.setIdTag(idTag);
            ins.setIdUser(idUser);
            ins.setIdInsulin(idInsulin);
            ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
            ins.setDateTime(registerDate);
            float insulinDose;
            try{
                insulinDose = Float.parseFloat(insulinUnits.getText().toString());
            }catch (Exception e){
                insulinInputLayout.setError(getString(R.string.glicInputError));
                insulinInputLayout.getEditText().requestFocus();
                throw new Exception("wrong parameter");
            }
            ins.setInsulinUnits(insulinDose);
            reg.Insulin_Save(ins);
        }
        reg.close();
        rdb.close();
    }
    public void addMealRead()throws Exception{
        Spinner tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        if ((insulinCalculator.getCarbsRatio() == 0) && imgUri == null) {
            // nothing to save
            return;
        }
        //Get id of user
        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getId();
        //Get id of selected tag
        String tag = null;
        if (tagSpinner != null) {
            tag = tagSpinner.getSelectedItem().toString();
        }
        int idTag = rdb.Tag_GetIdByName(tag);
        rdb.close();

        TextInputLayout mealInputLayout = (TextInputLayout) findViewById(R.id.meal_txt);
        int hcValue;
        try{
            hcValue = Integer.parseInt(mealInputLayout.getEditText().getText().toString());
        }catch (Exception e){
            mealInputLayout.setError(getString(R.string.glicInputError));
            mealInputLayout.requestFocus();
            throw new Exception("wrong parameter");
        }


        DB_Write reg = new DB_Write(this);
        if(isUpdate){
            carbsData.setCarbsValue(hcValue);
            carbsData.setIdTag(idTag);
            carbsData.setPhotoPath(imgUri != null ? imgUri.getPath() : null);
            carbsData.setDateTime(registerDate);
            reg.Carbs_Update(carbsData);
        }else{
            CarbsRec carb = new CarbsRec();
            carb.setIdUser(idUser);
            carb.setCarbsValue(hcValue);
            carb.setIdTag(idTag);
            carb.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
            carb.setDateTime(registerDate);
            reg.Carbs_Save(carb);
        }
        reg.close();
    }
    private void fillInsulinSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        ArrayList<String> allInsulins = new ArrayList<>();
        DB_Read rdb = new DB_Read(this);
        HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
        rdb.close();

        if (val != null) {
            for (int i : val.keySet()) {
                allInsulins.add(val.get(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allInsulins);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spinner != null) {
                spinner.setAdapter(adapter);
            }
        }
    }
    private void insertGlicMenu(){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        addContent(R.layout.glycemia_content_edit);
        findViewById(R.id.glycemia_txt).requestFocus();
        buttons.add(0, RegistryFields.GLICEMIA);
        setGlycemiaListeners();
    }
    private void insertCarbsMenu(){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        addContent(R.layout.meal_content_edit);
        findViewById(R.id.meal_txt).requestFocus();
        buttons.add(0, RegistryFields.CARBS);
        setMealListeners();
    }
    private void insertInsulinMenu(){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        addContent(R.layout.insulin_content_edit);
        buttons.add(0, RegistryFields.INSULIN);
        findViewById(R.id.insulin_admin).requestFocus();
        fillInsulinSpinner();
        setInsulinListeners();
    }
    private void setCarbsPressed(View v){
        insertCarbsMenu();
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
        if(buttons.contains(RegistryFields.GLICEMIA)){insertInsulinSuggestion(RegistryFields.CARBS);}
    }
    private void setGlicPressed(View v){
        insertGlicMenu();
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
        if(buttons.contains(RegistryFields.CARBS)){insertInsulinSuggestion(RegistryFields.GLICEMIA);}
    }
    private void setInsuPressed(View v){
        insertInsulinMenu();
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
    }
    private void setupBottomSheet() {
        //
        bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(RegistryFields.GLICEMIA)) {
                    removeContent(buttons.indexOf(RegistryFields.GLICEMIA));
                    buttons.remove(RegistryFields.GLICEMIA);
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
                } else {
                    setGlicPressed(v);
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(RegistryFields.CARBS)) {
                    removeContent(buttons.indexOf(RegistryFields.CARBS));
                    buttons.remove(RegistryFields.CARBS);
                    bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(false);
                } else {
                    setCarbsPressed(v);
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(RegistryFields.INSULIN)) {
                    removeContent(buttons.indexOf(RegistryFields.INSULIN));
                    buttons.remove(RegistryFields.INSULIN);
                    bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(false);
                } else {
                    setInsuPressed(v);
                }
            }
        });
    }

    private void insertInsulinSuggestion(RegistryFields activator){
        Boolean cons = ((!buttons.contains(RegistryFields.INSULIN)) && buttons.contains(RegistryFields.CARBS) && buttons.contains(RegistryFields.GLICEMIA));
        if (cons) {
            float insulinUnits = 0;
            addContentAt(R.layout.insulin_content_edit, buttons.size()-1);

            /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/

            buttons.add(buttons.size()-1, RegistryFields.INSULIN);
            View v = bottomSheetViewgroup.findViewById(R.id.bs_insulin);
            fillInsulinSpinner();
            Boolean carbReadExists = insulinCalculator.getCarbs()>0;
            Boolean glycReadExists = insulinCalculator.getGlycemia()>0;

            if(carbReadExists){
                if(glycReadExists){
                    insulinUnits = insulinCalculator.getInsulinTotal(useIOB);
                }else{
                    insulinUnits = insulinCalculator.getInsulinCarbs();
                }
            }else{
                if(glycReadExists){
                    insulinUnits = insulinCalculator.getInsulinGlycemia();
                }
            }

            if((insulinUnits>0) && (!getIsManual())){
                TextInputLayout insulinInput = ((TextInputLayout) findViewById(R.id.insulin_admin));
                insulinInput.setHintEnabled(true);
                TextView insuTxt = insulinInput.getEditText();
                insuTxt.removeTextChangedListener(getInsulinTW());
                insuTxt.requestFocus();
                insuTxt.setText(insulinUnits+"");
                if(activator.equals(RegistryFields.CARBS)){
                    findViewById(R.id.meal_txt).requestFocus();
                }else{
                    findViewById(R.id.glycemia_txt).requestFocus();
                }
                showCalcs();
            }
            setInsulinListeners();
            v.getAnimation();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
                }
            }, 100L);
            hideBottomSheet();
        }
    }
    private void toggleInsulinCalcDetails(View view) {
        expandInsulinCalcsAuto = false;
        if (!isFragmentShowing()) {
            showCalcs();
        } else {
            hideCalcs();
        }
    }
    private boolean isFragmentShowing() {
        return fragmentInsulinCalcsFragment != null;
    }
    private void refreshCalcs(){
        if(buttons.contains(RegistryFields.INSULIN)){
            showCalcs();
            if(isFragmentShowing()) {
                if(!getIsManual()){
                    float insulinUnits = 0;
                    if(insulinCalculator.getCarbs()>0) {
                        insulinUnits += insulinCalculator.getInsulinCarbs();
                    }
                    if(insulinCalculator.getGlycemia()>0){
                        insulinUnits += insulinCalculator.getInsulinGlycemia();
                    }
                    if(insulinUnits>0){
                        TextInputLayout insulinInput = ((TextInputLayout) findViewById(R.id.insulin_admin));
                        insulinInput.setHintEnabled(true);
                        EditText insuET = insulinInput.getEditText();
                        insuET.removeTextChangedListener(getInsulinTW());
                        insulinInput.getEditText().setText(insulinUnits+"");
                        insuET.addTextChangedListener(getInsulinTW());
                    }
                }
            }
        }

    }
    private void showCalcs() {

        hideBottomSheet();
        //hideKeyboard();

        if (fragmentInsulinCalcsFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
            if (fragment != null) {
                fragmentInsulinCalcsFragment = (InsulinCalcFragment) fragment;
            } else {
                fragmentInsulinCalcsFragment = InsulinCalcFragment.newInstance((int) insulinCalculator.getGlycemiaRatio(), (int) insulinCalculator.getCarbsRatio());
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_calcs, fragmentInsulinCalcsFragment)
                        .commit();
                fragmentManager.executePendingTransactions();

                ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1, Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, 0);
                animation.setDuration(700);
                FrameLayout fragmentCalcs = (FrameLayout) findViewById(R.id.fragment_calcs);
                if (fragmentCalcs != null) {
                    fragmentCalcs.startAnimation(animation);
                }
                ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
                if (calcInsulinInfo != null) {
                    calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_grey_900_24dp));
                }
            }
        }

        fragmentInsulinCalcsFragment.setCorrectionGlycemia(insulinCalculator.getInsulinGlycemia());
        fragmentInsulinCalcsFragment.setCorrectionCarbs(insulinCalculator.getInsulinCarbs());
        fragmentInsulinCalcsFragment.setResult(insulinCalculator.getInsulinTotal(useIOB), insulinCalculator.getInsulinTotal(useIOB, true));
        fragmentInsulinCalcsFragment.setInsulinOnBoard(insulinCalculator.getInsulinOnBoard());
        insulinCalculator.setListener(new InsulinCalculator.InsulinCalculatorListener() {
            @Override
            public void insulinOnBoardChanged(InsulinCalculator calculator) {
                if (fragmentInsulinCalcsFragment != null) {
                    showCalcs();
                }
            }
        });
    }
    private void hideCalcs() {
        if (fragmentInsulinCalcsFragment != null) {
            ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 0, Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, 0);
            animation.setDuration(700);
            FrameLayout fragmentCalcs = (FrameLayout) findViewById(R.id.fragment_calcs);
            if (fragmentCalcs != null) {
                fragmentCalcs.startAnimation(animation);
            }
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getFragmentManager().beginTransaction()
                            .remove(fragmentInsulinCalcsFragment)
                            .commit();
                    fragmentInsulinCalcsFragment = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            insulinCalculator.setListener(null);
            ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
            if (calcInsulinInfo != null) {
                calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_information_outline_grey600_24dp));
            }
        }
    }
    private Uri getImgURI(){
        File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
        File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                // unable to create directory
                // todo report and recover
            }
        }
        this.generatedImageUri = Uri.fromFile(file);
        return generatedImageUri;
    }
    public String getDate() {
        return registerDateTextV.getText().toString();
    }
    public String getTime() {
        return registerTimeTextV.getText().toString();
    }
    private void setImgURI(Uri newUri){imgUri=newUri;}
    private void imageRemoved() {
        setImgURI(null);
    }
    private void addGlycemiaObjective() {
        Intent intent = new Intent(this, TargetBG_detail.class);
        EditText targetGlycemia = ((TextInputLayout) findViewById(R.id.glycemia_obj)).getEditText();
        String goal = null;
        if (targetGlycemia != null) {
            goal = targetGlycemia.getText().toString();
        }
        if (!TextUtils.isEmpty(goal)) {
            float target = Float.parseFloat(goal);
            Bundle bundle = new Bundle();
            bundle.putFloat(TargetBG_detail.BUNDLE_GOAL, target);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private TextWatcher getInsulinTW(){
        TextWatcher ins = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setIsManual(true);
                TextInputLayout insulinInputLayout = (TextInputLayout) findViewById(R.id.insulin_admin);
                insulinInputLayout.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
        return ins;
    }
    private TextWatcher getCarbsTW(){
        TextWatcher carbsTW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String carbsS = editable.toString();
                if(carbsS != null){
                    try{
                        int carbs = Integer.parseInt(carbsS);
                        insulinCalculator.setCarbs(carbs);
                        Log.i(TAG, "afterTextChanged: carbs<------------");
                    }catch (NumberFormatException e){
                        insulinCalculator.setCarbs(0);
                    }
                    refreshCalcs();
                }
            }
        };
        return carbsTW;
    }
    private TextWatcher getGlicTW(){
        TextWatcher glicTW = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout glycValueT = (TextInputLayout) findViewById(R.id.glycemia_txt);
                glycValueT.setError("");
                String glycString = editable.toString();
                if(glycString != null){
                    try{
                        int glycValue = Integer.parseInt(glycString);
                        insulinCalculator.setGlycemia(glycValue);
                        Log.i(TAG, "afterTextChanged: glic<-");
                    }catch (NumberFormatException e){
                        // glycValueT.setError(R.string.glicInputError);
                        insulinCalculator.setGlycemia(0);
                    }
                    refreshCalcs();
                }
            }
        };
        return glicTW;
    }
    private TextWatcher getGlicObjTW(){
        TextWatcher objTW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String glycObjString = editable.toString();
                TextInputLayout glycObjT = (TextInputLayout) findViewById(R.id.glycemia_obj);
                glycObjT.setError("");
                if(glycObjString != null){
                    try{
                        int glycObjValue = Integer.parseInt(glycObjString);
                        insulinCalculator.setGlycemiaTarget(glycObjValue);
                        Log.i(TAG, "afterTextChanged: setTarget<-");
                    }catch (NumberFormatException e){
                        insulinCalculator.setGlycemiaTarget(0);
                    }
                    refreshCalcs();
                }
            }
        };
        return objTW;
    }

    private void setMealListeners(){
        ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imageView == null) {
            return;
        }
        if (imgUri == null) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            imageView.setImageBitmap(b);
        }
        TextView carbsTextView = (TextView) findViewById(R.id. meal);
        carbsTextView.addTextChangedListener(getCarbsTW());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {
                    final Intent intent = new Intent(getBaseContext(), ViewPhoto.class);
                    Bundle argsToPhoto = new Bundle();
                    argsToPhoto.putString("Path", imgUri.getPath());
                    argsToPhoto.putInt("Id", -1);
                    intent.putExtras(argsToPhoto);
                    startActivityForResult(intent, IMAGE_VIEW);
                } else {
                    try{
                        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImgURI());
                        startActivityForResult(intent, IMAGE_CAPTURE);
                    }catch (Exception e){
                        //error label -> permition denied
                    }
                }
            }
        });
    }
    public void setGlycemiaListeners(){

        String time = registerTimeTextV.getText().toString();
        MyDiabetesStorage storage = MyDiabetesStorage.getInstance(this);

        TextInputLayout glycValueT = (TextInputLayout) findViewById(R.id.glycemia_txt);
        glycValueT.getEditText().addTextChangedListener(getGlicTW());

        int objective = 0;
        TextInputLayout glycObjT = (TextInputLayout) findViewById(R.id.glycemia_obj);
        glycObjT.getEditText().addTextChangedListener(getGlicObjTW());

        try {
            objective = storage.getGlycemiaObjectives(time);
            glycObjT.getEditText().setText(objective+"");
            insulinCalculator.setGlycemiaTarget(objective);
        } catch (Exception e) {
            ImageButton plusButton = (ImageButton) findViewById(R.id.insert_new_glic_objective);
            plusButton.setVisibility(View.VISIBLE);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGlycemiaObjective();
                }
            });
        }
    }
    private void setInsulinListeners(){
        View insuInfo = findViewById(R.id.bt_insulin_calc_info);
        if (insuInfo != null) {
            insuInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleInsulinCalcDetails(v);
                }
            });
        }
        TextInputLayout insulinDose = (TextInputLayout) findViewById(R.id.insulin_admin);
        TextView insuTxt = insulinDose.getEditText();
        insuTxt.addTextChangedListener(getInsulinTW());

    }

    private void insertInsulinData(float insulinUnits){

        TextInputLayout insulinInput = ((TextInputLayout) findViewById(R.id.insulin_admin));
        TextView insuTxt = insulinInput.getEditText();
        insuTxt.removeTextChangedListener(getInsulinTW());
        insuTxt.requestFocus();
        insuTxt.setText(insulinUnits+"");
        insuTxt.addTextChangedListener(getInsulinTW());
//        showCalcs();
    }
    private void insertGlicData(int glicValue, int glicObjValue){
        TextInputLayout glicInput = ((TextInputLayout) findViewById(R.id.glycemia_txt));
        TextView glicTxt = glicInput.getEditText();
        glicTxt.requestFocus();
        glicTxt.setText(glicValue+"");
        glicTxt.addTextChangedListener(getGlicTW());

        TextInputLayout glicObjInput = ((TextInputLayout) findViewById(R.id.glycemia_obj));
        TextView glicObjTxt = glicObjInput.getEditText();
        glicObjTxt.requestFocus();
        glicObjTxt.setText(glicObjValue+"");
        glicObjTxt.addTextChangedListener(getGlicObjTW());
    }
    private void insertGlicData(int glicValue){
        TextInputLayout glicInput = ((TextInputLayout) findViewById(R.id.glycemia_txt));
        TextView glicTxt = glicInput.getEditText();
        glicTxt.requestFocus();
        glicTxt.setText(glicValue+"");
        glicTxt.addTextChangedListener(getGlicTW());
    }

    private void insertCarbsData(int carbValue){
        final ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imageView == null) {
            return;
        }
        if (imgUri != null) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            imageView.setImageBitmap(b);
        }
        TextInputLayout carbsInput = (TextInputLayout) findViewById(R.id.meal_txt);
        TextView carbsTextView = carbsInput.getEditText();
        carbsTextView.requestFocus();
        carbsTextView.setText(carbValue+"");
        carbsTextView.addTextChangedListener(getCarbsTW());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {
                    final Intent intent = new Intent(getBaseContext(), ViewPhoto.class);
                    Bundle argsToPhoto = new Bundle();
                    argsToPhoto.putString("Path", imgUri.getPath());
                    argsToPhoto.putInt("Id", -1);
                    intent.putExtras(argsToPhoto);
                    startActivityForResult(intent, IMAGE_VIEW);
                } else {
                    try{
                        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImgURI());
                        startActivityForResult(intent, IMAGE_CAPTURE);
                    }catch (Exception e){
                        //error label -> permition denied
                    }
                }
            }
        });
    }
    private void fillParameters(Bundle args){
        if (args != null) {
            isUpdate = true;
            if (args.containsKey(ARG_CARBS)) {
                carbsData = args.getParcelable(ARG_CARBS);
            }
            if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
                glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
            }

            if (args.containsKey(ARG_INSULIN)) {
                insulinData = args.getParcelable(ARG_INSULIN);
            }

            noteId = -1;
            DB_Read db_read = new DB_Read(this);
            if (carbsData != null) {
                carbsData = db_read.CarboHydrate_GetById(carbsData.getId());
                if (carbsData != null) {
                    Log.i(TAG, "fillParameters: carbs:"+carbsData.getCarbsValue());
                    String imgPath = carbsData.getPhotoPath();
                    if(imgPath!=null){imgUri = Uri.parse(imgPath);}
                    insertCarbsMenu();
                    insertCarbsData(carbsData.getCarbsValue());
                    noteId = carbsData.getIdNote();
                    registerDate = carbsData.getDateTime();
                }
            }
            if (glycemiaData != null) {
                glycemiaData = db_read.Glycemia_GetById(glycemiaData.getId());
                if (glycemiaData != null) {
                    Log.i(TAG, "fillParameters: glic:"+glycemiaData.getValue());
                    insertGlicMenu();
                    insertGlicData(glycemiaData.getValue());
                    noteId = glycemiaData.getIdNote();
                    registerDate = glycemiaData.getDateTime();
                }
            }
            if (insulinData != null) {
                insulinData = db_read.InsulinReg_GetById(insulinData.getId());
                if (insulinData != null) {
                    Log.i(TAG, "fillParameters: insulin:"+insulinData.getInsulinUnits());
                    insertInsulinMenu();
                    insertInsulinData(insulinData.getInsulinUnits());
                    noteId = insulinData.getIdNote();
                    registerDate = insulinData.getDateTime();
                    //TODO insulinData.getTargetGlycemia();
                }
            }
            if(registerDate!=null){
                Log.i(TAG, "fillParameters: Calendar day"+registerDate.get(Calendar.DAY_OF_MONTH)+" hour:"+registerDate.get(Calendar.HOUR_OF_DAY));
                setDate(registerDate);
                setTime(registerDate);
            }
            db_read.close();
            insulinCalculator = new InsulinCalculator(this);
            insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
            insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
            insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);
            Calendar timeCalendar = DateUtils.getTimeCalendar(time);
            if (timeCalendar != null) {
                insulinCalculator.setTime(this, timeCalendar.get(Calendar.HOUR_OF_DAY), timeCalendar.get(Calendar.MINUTE), date);
            }
            hideBottomSheet();
            hideKeyboard();
        }
    }
}

/* public void setupInsulinCalculator() {
        this.insulinCalculator = getInsulinCalculator();
        if (insulinCalculator.getCarbs() > 0) {
            carbs.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getCarbs()));
        }
        if (insulinCalculator.getGlycemia() > 0) {
            glycemia.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getGlycemia()));
        }
        if (insulinCalculator.getInsulinTarget() > 0) {
            target.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getInsulinTarget()));
        }
        setGlycemiaTarget(insulinCalculator.getInsulinTarget());
        float insulinTotal = insulinCalculator.getInsulinTotal(useIOB, true);
        if (insulinTotal > 0) {
            insulinIntake.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinTotal));
        }
    }*/
