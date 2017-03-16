package pt.it.porto.mydiabetes.ui.activities;

/**
 * Created by Diogo on 22/02/2017.
 */


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
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
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class NewHomeRegistry extends AppCompatActivity implements InsulinCalcFragment.CalcListener {

    @Override
    public void setup() {
        fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
        showCalcs();
    }

    private enum RegistryFields{MEAL,INSULIN,GLICEMIA,PLUS};
    private boolean usetIOB;

    private LinearLayout bottomSheetViewgroup;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout contentLayout;
    private ArrayList<RegistryFields> buttons;

    private Calendar registerDate;

    private TextView registerDateTextV;
    private TextView registerTime;
    private int carbValue = 0;
    private int glicValue = 0;
    private int glicObjectiveValue = 0;

    protected InsulinCalcFragment fragmentInsulinCalcsFragment;
    protected InsulinCalculator insulinCalculator = null;
    boolean useIOB = true;
    private boolean expandInsulinCalcsAuto = false;
    String TAG = "newREG";

    public final static int IMAGE_CAPTURE = 2;
    public final static int IMAGE_VIEW = 3;

    private static final String CALCS_OPEN = "calcs open";
    private static final String GENERATED_IMAGE_URI = "generated_image_uri";
    private Uri generatedImageUri;
    private Uri imgUri;
    private Bitmap b;

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
        FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));
        useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        setContentView(R.layout.activity_add_event);
        imgUri = null;

        bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        contentLayout = (LinearLayout) findViewById(R.id.content_panel);
        registerDateTextV = (TextView) findViewById(R.id.registryDate);
        registerTime = (TextView) findViewById(R.id.registerTime);

        bottomSheetViewgroup.setVisibility(View.INVISIBLE);
        buttons = new ArrayList<>();
        buttons.add(RegistryFields.PLUS);
        bottomSheetViewgroup.setVisibility(View.VISIBLE);

        insulinCalculator = new InsulinCalculator(this);

        registerDateTextV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				showDatePickerDialog(view);
            }
		});
        String currentTime = android.text.format.DateFormat.getTimeFormat(this.getApplicationContext()).format(new java.util.Date());

        registerTime.setText( currentTime);
        registerTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        (findViewById(R.id.bt_add_more_content)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showBottomSheet();
            }
        });
        ((Spinner) findViewById(R.id.tag_spinner)).setAdapter(new StringSpinnerAdapter(this, getResources().getStringArray(R.array.daytimes)));
        setupBottomSheet();
        Calendar time = Calendar.getInstance();
        setDate(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
        registerDate.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        registerDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
    }
    private void showTimePickerDialog(View v) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                registerDate.set(Calendar.HOUR_OF_DAY,selectedHour);
                registerDate.set(Calendar.MINUTE,selectedMinute);

                final Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, selectedHour);
                c.set(Calendar.MINUTE, selectedMinute);
                String timeString = DateUtils.getFormattedTime(c);
                registerTime.setText( timeString);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.select_time));
        mTimePicker.show();
    }
    private void showDatePickerDialog(View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                v.getContext(),
                R.style.style_date_picker_dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        setDate(year, month, day);
                    }
                },
                registerDate.get(Calendar.YEAR),
                registerDate.get(Calendar.MONTH),
                registerDate.get(Calendar.DAY_OF_MONTH)){

//                @Override
//                protected void onCreate(Bundle savedInstanceState)
//                {
//                    super.onCreate(savedInstanceState);
//                    int year = getContext().getResources()
//                        .getIdentifier("android:id/year", null, null);
//                    if(year != 0){  View yearPicker = findViewById(year);
//                        if(yearPicker != null){
//                            yearPicker.setVisibility(View.GONE);
//                        }
//                    }
//                }
            };
        datePickerDialog.show();
    }
    private void setDate(int year, int month, int day) {
        registerDate = new GregorianCalendar(year, month, day);

        StringBuilder displayDate = new StringBuilder(18);
        displayDate.append(registerDate.get(Calendar.DAY_OF_MONTH));
        displayDate.append(" ");
        displayDate.append(registerDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        registerDateTextV.setText(displayDate.toString());
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
    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet();
        } else {
            super.onBackPressed();
        }
    }
    private void showBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetViewgroup.setVisibility(View.VISIBLE);
    }
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetViewgroup.setVisibility(View.GONE);
    }
    private void save() {
        validateInfo_Save();
        finish();
    }
    private void validateInfo_Save(){
        TextView time = (TextView) findViewById(R.id.registerTime);
        if (registerDate != null && time != null) {

            //SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.iso8601Format.toPattern());//yyyy-MM-dd HH:mm
            //String currDate = registerDate.get(Calendar.YEAR)+"-"+registerDate.get(Calendar.MONTH)+"-"+registerDate.get(Calendar.DAY_OF_MONTH)+" "+time.getText();
        }
        for(RegistryFields field:buttons){
            switch (field){
                case MEAL:
                    AddMealRead();
                    break;
                case GLICEMIA:
                    AddGlycemiaRead();
                    break;
                case INSULIN:
                    AddInsulinRead();
                    break;
            }
        }
        setResult(Home.CHANGES_OCCURRED, this.getIntent());
    }
    public void AddGlycemiaRead() {

        Spinner tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        EditText glycemia = (EditText) findViewById(R.id.glycemia);

        if (glycemia != null && glycemia.getText().toString().equals("")) {
            glycemia.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
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

        DB_Write reg = new DB_Write(this);
        GlycemiaRec gly = new GlycemiaRec();

        gly.setIdUser(idUser);
        if (glycemia != null) {
            gly.setValue(Integer.parseInt(glycemia.getText().toString()));
        }
        gly.setDateTime(registerDate);
        gly.setIdTag(idTag);
        reg.Glycemia_Save(gly);
        reg.close();
    }
    public void AddInsulinRead() {

        Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        EditText insulinunits = (EditText) findViewById(R.id.insulin_intake);

        Spinner tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        //tem de ter um target inserido
        DB_Read read = new DB_Read(this);
        if (!read.Target_HasTargets()) {
            read.close();
            //TODO ShowDialogAddTarget();
            return;
        }
        if (insulinunits.getText().toString().equals("")) {
            insulinunits.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(insulinunits, InputMethodManager.SHOW_IMPLICIT);
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
        InsulinRec ins = new InsulinRec();


        ins.setIdUser(idUser);
        ins.setIdInsulin(idInsulin);
        ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
        ins.setDateTime(registerDate);
        //ins.setTargetGlycemia(insulinCalculator.getInsulinTarget());
        ins.setInsulinUnits(Float.parseFloat(insulinunits.getText().toString()));

        reg.Insulin_Save(ins);

        reg.close();
        rdb.close();
    }
    public void AddMealRead() {
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
            //Log.d("selected Spinner", tag);
        }
        int idTag = rdb.Tag_GetIdByName(tag);
        rdb.close();

        DB_Write reg = new DB_Write(this);
        CarbsRec carb = new CarbsRec();
        carb.setIdUser(idUser);
        carb.setCarbsValue(insulinCalculator.getCarbs());
        carb.setIdTag(idTag);
        carb.setPhotoPath(imgUri != null ? imgUri.getPath() : null); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
        carb.setDateTime(registerDate);
        reg.Carbs_Save(carb);
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
                    addContent(R.layout.glycemia_content_edit);
                    buttons.add(0, RegistryFields.GLICEMIA);
                    if(buttons.contains(RegistryFields.MEAL)){insertInsulinSuggestion();}
                    setGlycemiaListeners();
                    v.getAnimation();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
                        }
                    }, 100L);
                    hideBottomSheet();
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(RegistryFields.MEAL)) {
                    removeContent(buttons.indexOf(RegistryFields.MEAL));
                    buttons.remove(RegistryFields.MEAL);
                    bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(false);
                } else {
                    addContent(R.layout.meal_content_edit);
                    buttons.add(0, RegistryFields.MEAL);
                    if(buttons.contains(RegistryFields.GLICEMIA)){insertInsulinSuggestion();}
                    setMealListeners();
                    v.getAnimation();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
                        }
                    }, 100L);
                    hideBottomSheet();
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
                    addContent(R.layout.insulin_content_edit);
                    buttons.add(0, RegistryFields.INSULIN);
                    fillInsulinSpinner();
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
        });
    }
    private void insertInsulinSuggestion(){
        Boolean cons = ((!buttons.contains(RegistryFields.INSULIN)) && buttons.contains(RegistryFields.MEAL) && buttons.contains(RegistryFields.GLICEMIA));
        if (cons) {
            addContentAt(R.layout.insulin_content_edit, buttons.size()-1);
            View v = bottomSheetViewgroup.findViewById(R.id.bs_insulin);
            buttons.add(buttons.size()-1, RegistryFields.INSULIN);
            fillInsulinSpinner();
            setInsulinListeners();
            float insulinUnits = 0;
            if(carbValue!=0){
                if(glicValue!=0){
                    insulinUnits = insulinCalculator.getInsulinTotal(useIOB);
                }else{
                    insulinUnits = insulinCalculator.getInsulinCarbs();
                }
            }else{
                insulinUnits = insulinCalculator.getInsulinGlycemia();
            }
            if(insulinUnits!=0){
                ((TextView) findViewById(R.id.insulin_intake)).setText(insulinUnits+"");
                showCalcs();
            }
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
        carbsTextView.addTextChangedListener(new TextWatcher() {
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
                        carbValue = carbs;
                        refreshCalcs();
                    }catch (NumberFormatException e){
                        insulinCalculator.setCarbs(0);
                        carbValue = 0;
                    }

                }
            }
        });
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

        String time = registerTime.getText().toString();
        MyDiabetesStorage storage = MyDiabetesStorage.getInstance(this);

        TextInputLayout glycValueT = (TextInputLayout) findViewById(R.id.glycemia_txt);
        glycValueT.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String glycString = editable.toString();
                if(glycString != null){
                    try{
                        int glycValue = Integer.parseInt(glycString);
                        insulinCalculator.setGlycemia(glycValue);
                        glicValue = glycValue;
                        refreshCalcs();
                    }catch (NumberFormatException e){
                        insulinCalculator.setGlycemia(0);
                        glicValue = 0;
                    }

                }
            }
        });

        int objective = 0;
        TextInputLayout view = (TextInputLayout) findViewById(R.id.glycemia_obj);

        view.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String glycObjString = editable.toString();
                if(glycObjString != null){
                    try{
                        int glycObjValue = Integer.parseInt(glycObjString);
                        insulinCalculator.setGlycemiaTarget(glycObjValue);
                        glicObjectiveValue = glycObjValue;
                        refreshCalcs();
                    }catch (NumberFormatException e){
                        insulinCalculator.setGlycemiaTarget(0);
                        glicObjectiveValue = 0;
                    }

                }
            }
        });

        try {
            objective = storage.getGlycemiaObjectives(time);
            view.getEditText().setText(objective+"");
            insulinCalculator.setGlycemiaTarget(objective);
            glicObjectiveValue =  objective;
        } catch (Exception e) {
            ImageButton plusButton = (ImageButton) findViewById(R.id.insert_new_glic_objective);
            plusButton.setVisibility(View.VISIBLE);
            glicObjectiveValue =  0;
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGlycemiaObjective();
                }
            });
        }
    }
    private void setInsulinListeners(){
        View view = findViewById(R.id.bt_insulin_calc_info);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleInsulinCalcDetails(v);
                }
            });
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
        if(isFragmentShowing()){
            fragmentInsulinCalcsFragment.setCorrectionGlycemia(insulinCalculator.getInsulinGlycemia());
            fragmentInsulinCalcsFragment.setCorrectionCarbs(insulinCalculator.getInsulinCarbs());
            fragmentInsulinCalcsFragment.setResult(insulinCalculator.getInsulinTotal(useIOB), insulinCalculator.getInsulinTotal(useIOB, true));
            fragmentInsulinCalcsFragment.setInsulinOnBoard(insulinCalculator.getInsulinOnBoard());
        }
    }
    private void showCalcs() {

        hideBottomSheet();
        hideKeyboard();

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
        return registerTime.getText().toString();
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
}
