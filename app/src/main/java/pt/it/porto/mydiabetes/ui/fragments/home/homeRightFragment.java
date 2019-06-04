package pt.it.porto.mydiabetes.ui.fragments.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import info.abdolahi.CircularMusicProgressBar;
import lecho.lib.hellocharts.model.Line;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.ChartSection;
import pt.it.porto.mydiabetes.ui.activities.MyData;
import pt.it.porto.mydiabetes.ui.activities.Statistics;
import pt.it.porto.mydiabetes.ui.createMeal.activities.CreateMealActivity;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.FileProvider;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment {

    private UserInfo myData;
    private String userImgFileName = "profilePhoto";
    private View layout;
    final int THUMBSIZE = 350;

    private ImageButton helpButton;
    private ImageView beginnerBadge;
    private TextView beginnerBadgesText;
    private ImageView mediumBadge;
    private TextView mediumBadgesText;
    private ImageView advancedBadge;
    private TextView advancedBadgesText;
    private ImageView currentBadge;

    private ImageButton helpButtonPersonal;
    private TextView averageText;
    private TextView variabilityText;
    private TextView dailyRecordNumber;
    private TextView streak_days;
    private TextView records_left;
    private LinearLayout startRecordsMessage;
    private LinearLayout streakDaysMessage;
    private LinearLayout leftDaysMessage;
    private LinearLayout congratsMessage;

    private Uri currentImageUri;

    private TextView levelText;
    private TextView pointsText;
    private CircularMusicProgressBar mCircleView;

    private int countBeginner;
    private int countMedium;
    private int countAdvanced;
    //private int countDaily;
    private BadgeRec dailyBadge;


    private static final int REQUEST_TAKE_PHOTO = 6;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 7;
    private static final int RIGHT_FRAGMENT_PICTURE = 8;


    private Bitmap bmp;
    private List<Image> images = new ArrayList<>();

    public static pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment fragment = new pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment();
        return fragment;
    }

    public homeRightFragment() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this.getContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == RIGHT_FRAGMENT_PICTURE){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
                if(profile_img.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(profile_img.getAbsolutePath());
                    mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bmp, THUMBSIZE, THUMBSIZE, false));
                }
            }else{
                Toast.makeText(this.getContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void medals_display_init(){
        DB_Read read = new DB_Read(getContext());

        int points = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int lvl = LevelsPointsUtils.getLevel(getContext(),read);

        int nextLvlPoints = LevelsPointsUtils.getPointsNextLevel(getContext(),read);
        int totalPoints = LevelsPointsUtils.getTotalPoints(getContext(), read);

        myData = read.MyData_Read();
        //LinkedList<BadgeRec> list = read.Badges_GetAll();

        countBeginner = read.Badges_GetCount("beginner");
        countMedium = read.Badges_GetCount("medium");
        countAdvanced = read.Badges_GetCount("advanced");
        //countDaily = read.Badges_GetCount("daily");
        dailyBadge = read.getLastDailyMedal();

        read.close();

        mCircleView = (CircularMusicProgressBar) layout.findViewById(R.id.circleView);
        mCircleView.setValue(points);

        levelText = (TextView) layout.findViewById(R.id.numberLevel);
        levelText.setText(lvl+"");
        pointsText = (TextView) layout.findViewById(R.id.numberPoints);
        pointsText.setText(totalPoints+" / "+nextLvlPoints);

        beginnerBadge = (ImageView) layout.findViewById(R.id.beginnerBadge);
        beginnerBadgesText = (TextView) layout.findViewById(R.id.beginnerBadgesText);
        mediumBadgesText = (TextView) layout.findViewById(R.id.beginnerBadgesText);

        if(countBeginner>0){
            beginnerBadge.setImageResource(R.drawable.medal_gold_beginner);
        }


        if( lvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            mediumBadge = (ImageView) layout.findViewById(R.id.mediumBadge);
            mediumBadge.setImageResource(R.drawable.medal_gold_medium);
            mediumBadgesText = (TextView) layout.findViewById(R.id.mediumBadgesText);

            if(lvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
                advancedBadge = (ImageView) layout.findViewById(R.id.advancedBadge);
                advancedBadge.setImageResource(R.drawable.medal_gold_advanced);
                advancedBadgesText = (TextView) layout.findViewById(R.id.advancedBadgesText);
            }
        }
        helpButton = (ImageButton) layout.findViewById(R.id.helpButton);
        currentBadge = (ImageView) layout.findViewById(R.id.currentBadge);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home_right, container, false);

        medals_display_init();

        setImage();
        setMyDataFromDB(myData);
        updateMedals();

        helpButtonPersonal = (ImageButton) layout.findViewById(R.id.helpButtonPersonal);
        CardView personalInfo = (CardView) layout.findViewById(R.id.personalInfo);
        CardView badgesInfo = (CardView) layout.findViewById(R.id.badgesInfo);
        final LinearLayout competitionInfo = (LinearLayout) layout.findViewById(R.id.competitionInfo);
        final Button hideShowCompetition = (Button) layout.findViewById(R.id.hideShowCompetition);

        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyData.class);
                startActivity(intent);
            }
        });

        badgesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Badges.class);
                startActivity(intent);
            }
        });

        helpButtonPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setDescription(getString(R.string.badge_help_personal_dialog_desc))
                        .setStyle(Style.HEADER_WITH_ICON)
//                        .setIcon(R.drawable.medal_gold_record_a)
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .withIconAnimation(false)
                        .setCancelable(true)
                        .setPositiveText(R.string.okButton)
                        .show();
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setTitle(getString(R.string.badge_help_dialog_title))
                        .setDescription(getString(R.string.badge_help_dialog_desc))
                        //.setStyle(Style.HEADER_WITH_TITLE)
//                        .setIcon(R.drawable.medal_gold_record_a)
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .withIconAnimation(false)
                        .setCancelable(true)
                        .setPositiveText(R.string.okButton)
                        .show();
            }
        });

        hideShowCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (competitionInfo.getVisibility() == View.GONE) {
                    competitionInfo.setVisibility(View.VISIBLE);
                    hideShowCompetition.setBackgroundColor(getResources().getColor(R.color.primary_light));
                    hideShowCompetition.setText("Minimizar secção competitiva");
                } else {
                    competitionInfo.setVisibility(View.GONE);
                    hideShowCompetition.setBackgroundColor(getResources().getColor(R.color.white_background));
                    hideShowCompetition.setText("Mostrar secção competitiva");
                }
            }
        });

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            setProfilePhoto();

            DB_Read rdb = new DB_Read(getContext());
            BadgeUtils.addPhotoBadge(getContext(), rdb);
            rdb.close();
            updateMedals();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

        DB_Read read = new DB_Read(getContext());

        int percentageLvL = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int pointsToNextLvL = LevelsPointsUtils.getPointsNextLevel(getContext(),read);
        myData = read.MyData_Read();

        countBeginner = read.Badges_GetCount("beginner");
        countMedium = read.Badges_GetCount("medium");
        countAdvanced = read.Badges_GetCount("advanced");
        dailyBadge = read.getLastDailyMedal();

        String numberMedals_total = LevelsPointsUtils.getTotalPoints(getContext(), read)+" / "+pointsToNextLvL;

        read.close();

        mCircleView.setValue(percentageLvL);
        pointsText.setText(numberMedals_total);
        setMyDataFromDB(myData);
        updateMedals();
        setPersonalInfo();

    }

    private void updateMedals() {
        if(dailyBadge != null){
            String medalType = "medal_"+dailyBadge.getMedal()+"_"+dailyBadge.getType();
            currentBadge.setImageResource(getContext().getResources().getIdentifier(medalType,"drawable",getContext().getPackageName()));
        }
        if(countBeginner > 0){
            beginnerBadge.setImageResource(R.drawable.medal_gold_beginner);
            beginnerBadgesText.setText(countBeginner+"/23");
        }
        if(countMedium > 0){
            mediumBadge.setImageResource(R.drawable.medal_gold_medium);
            mediumBadgesText.setText(countMedium+"/21");
        }
        if(countAdvanced > 0){
            advancedBadge.setImageResource(R.drawable.medal_gold_advanced);
            advancedBadgesText.setText(countAdvanced+"/21");
        }
    }

    private void setImage() {
        mCircleView = (CircularMusicProgressBar) layout.findViewById(R.id.circleView);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
            if(profile_img.exists()){
                Bitmap bmp = BitmapFactory.decodeFile(profile_img.getAbsolutePath());
                mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bmp, THUMBSIZE, THUMBSIZE, false));
            }
        }else{
            //requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RIGHT_FRAGMENT_PICTURE);
        }



        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA + Manifest.permission.WRITE_EXTERNAL_STORAGE + Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

            }
        });
    }
    private void dispatchTakePictureIntent() throws IOException {
        File picFile = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
        picFile.createNewFile(); // Eduardo was here :c
        if(android.os.Build.VERSION.SDK_INT>=24){
            currentImageUri = FileProvider.getUriForFile(this.getContext(), BuildConfig.APPLICATION_ID+".provider", picFile);
        }else{
            currentImageUri = Uri.fromFile(picFile);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    public void setMyDataFromDB(UserInfo obj) {
        if (obj != null) {
            TextView name = (TextView) layout.findViewById(R.id.name);
            Calendar bday = DateUtils.getDateCalendar(obj.getBirthday());
            int age = DateUtils.getAge(bday);
            name.setTag(obj.getId());
            name.setText(obj.getUsername()+", "+age);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = userImgFileName;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void setProfilePhoto() {

        try {


            Bitmap pic_bitmap;
            if(android.os.Build.VERSION.SDK_INT>=28){
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContext().getContentResolver(), currentImageUri);
                pic_bitmap = ImageDecoder.decodeBitmap(source);
            }else{
                InputStream input = this.getContext().getContentResolver().openInputStream(currentImageUri);
                if (input == null) {
                    Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
                    return;
                }
                pic_bitmap = BitmapFactory.decodeStream(input);
            }

            File photoFile = null;
            try {
                photoFile = createImageFile();
                try (FileOutputStream out = new FileOutputStream(photoFile)) {
                    pic_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    out.flush();
                    out.close();

                    displayImg(photoFile.getAbsolutePath());

                    //Log.i("cenas", "setProfilePhoto: -> "+photoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void displayImg(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, THUMBSIZE, THUMBSIZE, false));

//        if(thumbnailPhotoView.getVisibility() == View.GONE) {
//            cameraPlaceholder.setVisibility(View.GONE);
//            thumbnailPhotoView.setVisibility(View.VISIBLE);
//        }
    }

    private void setPersonalInfo() {

        averageText = (TextView) layout.findViewById(R.id.averageText);
        variabilityText = (TextView) layout.findViewById(R.id.variabilityText);


        DB_Read rdb = new DB_Read(getContext());
        ArrayList<Integer> infoToday = rdb.getPersonalInfo(0);
        UserInfo userInfo = rdb.MyData_Read();
        rdb.close();
        if (infoToday.size() != 0) {

            int averageToday = infoToday.get(0);
            int variabiToday = (int) (infoToday.get(1)*100/infoToday.get(0));
            int hyposToday = infoToday.get(2);
            int hypersToday = infoToday.get(3);

            averageText.setText(String.valueOf(averageToday));
            variabilityText.setText(String.valueOf(variabiToday));

            // painting text given the quality of the values
            int hypo = userInfo.getLowerRange();
            int hyper = userInfo.getHigherRange();
            // average values
            if (averageToday < hypo || averageToday > hyper) {
                averageText.setTextColor(getResources().getColor(R.color.red));
            } else if ((averageToday >= hypo && averageToday < hypo+20) || (averageToday > hyper-20 && averageToday <= hyper)) {
                averageText.setTextColor(getResources().getColor(R.color.orange));
            } else {
                averageText.setTextColor(getResources().getColor(R.color.green));
            }
            //if (averageToday <= 60 || averageToday >= 190) averageText.setTextColor(getResources().getColor(R.color.red));
            //else if ((averageToday > 60 && averageToday < 80) || (averageToday > 170 && averageToday < 190)) averageText.setTextColor(getResources().getColor(R.color.orange));
            //else averageText.setTextColor(getResources().getColor(R.color.green));

            // variability values
            if (variabiToday > 40) variabilityText.setTextColor(getResources().getColor(R.color.red));
            else if (variabiToday > 36 && variabiToday <= 40) variabilityText.setTextColor(getResources().getColor(R.color.orange));
            else variabilityText.setTextColor(getResources().getColor(R.color.green));
        }
        getStreakValue();
    }

    public void getStreakValue() {
        streak_days = (TextView) layout.findViewById(R.id.streak_days);
        records_left = (TextView) layout.findViewById(R.id.records_left);
        dailyRecordNumber = (TextView) layout.findViewById(R.id.dailyRecordNumber);
        leftDaysMessage = (LinearLayout) layout.findViewById(R.id.leftDaysMessage);
        congratsMessage = (LinearLayout) layout.findViewById(R.id.congratsMessage);
        startRecordsMessage = (LinearLayout) layout.findViewById(R.id.startRecordsMessage);
        streakDaysMessage = (LinearLayout) layout.findViewById(R.id.streakDaysMessage);

        boolean inStreak = true;
        int recordGoal = 6;
        int streakDays = 0;
        int nRecords = 0;
        int day = 0;
        DB_Read rdb = new DB_Read(getContext());
        while (inStreak) {
            nRecords = rdb.getGlyRecordsNumberByDay(day);
            if (day == 0) { //get current day number of gly recordsD

                // if the user reach the goal
                if (nRecords >= recordGoal) {
                    leftDaysMessage.setVisibility(View.GONE);
                    congratsMessage.setVisibility(View.VISIBLE);
                } else {
                    leftDaysMessage.setVisibility(View.VISIBLE);
                    congratsMessage.setVisibility(View.GONE);
                    records_left.setText((recordGoal - nRecords) + " " + getResources().getQuantityString(R.plurals.numberOfGlyc, recordGoal - nRecords));
                }
                dailyRecordNumber.setText(String.valueOf(nRecords)+" / "+recordGoal);
                dailyRecordNumber.setTextColor(getResources().getColor(R.color.green));
                day++;
            } else if (nRecords >= recordGoal) { // get the streak based on the previous days
                streakDays++;
                day++;
            } else inStreak = false;
        }

        rdb.close();
        // if there're not days in streak show motivional quote, if there're, write how much
        if (streakDays == 0) {
            streakDaysMessage.setVisibility(View.GONE);
            startRecordsMessage.setVisibility(View.VISIBLE);
        } else {
            startRecordsMessage.setVisibility(View.GONE);
            streakDaysMessage.setVisibility(View.VISIBLE);
            streak_days.setText(streakDays + " " + getResources().getQuantityString(R.plurals.numberOfDays, streakDays));
        }
    }
}
