package com.mnemonicizer.mnemonicizer.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mnemonicizer.mnemonicizer.Model.Word;
import com.mnemonicizer.mnemonicizer.R;
import com.mnemonicizer.mnemonicizer.utils.DataBaseHelper;
import com.wang.avi.AVLoadingIndicatorView;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.TextToSpeechCallback;

import java.util.List;

public class WordViewActivity extends AppCompatActivity implements View.OnClickListener,SpeechDelegate {
    private static final int PERMISSIONS_REQUEST = 1 ;
    TextView word,word_nm,mng,word_mng,syno,anto,count_tv,ex1,ex2;
ImageButton play,rec,tick;
FloatingActionButton fav;
    private AVLoadingIndicatorView avi;
DataBaseHelper dataBaseHelper;
Word wordObj;
    private int cmpltWords,totalWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_view);
        dataBaseHelper = new DataBaseHelper(this);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        avi = findViewById(R.id.avi);
        avi.smoothToHide();
        ex1 = findViewById(R.id.ex1);
        ex2 = findViewById(R.id.ex2);
        fav = findViewById(R.id.fav);
        count_tv = findViewById(R.id.count_tv);
        play = findViewById(R.id.play);
        rec = findViewById(R.id.rec);
        tick = findViewById(R.id.tick);
        word = findViewById(R.id.word);
        word_nm = findViewById(R.id.word_name);
        mng = findViewById(R.id.mng);
        word_mng = findViewById(R.id.word_mng);
        syno = findViewById(R.id.synm);
        anto = findViewById(R.id.anym);
        wordObj = (Word) getIntent().getSerializableExtra("word");
        word.setText(wordObj.getName());
        word_nm.setText(wordObj.getName());
        mng.setText(wordObj.getMeaning());
        word_mng.setText(wordObj.getMeaning());
        syno.setText(wordObj.getSynonym());
        anto.setText(wordObj.getAntonym());
        ex1.setText(wordObj.getEx1());
        ex2.setText(wordObj.getEx2());
        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFav();
        setCmpltWords();
        setCount();
        play.setOnClickListener(this);
        rec.setOnClickListener(this);
        fav.setOnClickListener(this);
    }

    private void setCount() {
        Cursor c = dataBaseHelper.getAllCmplt();
        cmpltWords = c.getCount();
        Cursor t = dataBaseHelper.getAllWords();
        totalWords = t.getCount();
        count_tv.setText(cmpltWords+"/"+totalWords);
    }

    private void setFav() {
        if(dataBaseHelper.isFav(wordObj.getId()) == 1){
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_in));
        }else{
            fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        }
    }
    private void setCmpltWords() {
        if(dataBaseHelper.isCmplt(wordObj.getId()) == 1){
            tick.setImageDrawable(getResources().getDrawable(R.drawable.tick));
        }else{
            tick.setImageDrawable(getResources().getDrawable(R.drawable.tick_red));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.play:
                Speech.getInstance().say(wordObj.getName().trim(), new TextToSpeechCallback() {
                    @Override
                    public void onStart() {
                        Log.i("speech", "speech started");
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("speech", "speech completed");
                    }

                    @Override
                    public void onError() {
                        Log.i("speech", "speech error");
                    }
                });
                break;
            case R.id.rec:
                checkPermission();
                break;
            case R.id.fav:
                if(dataBaseHelper.isFav(wordObj.getId()) == 1){
                    if(dataBaseHelper.removeFav(wordObj.getId()) == 1)
                        fav.setImageDrawable(getResources().getDrawable(R.drawable.fav));
                }else{
                    if(dataBaseHelper.addFav(wordObj.getId()) == 1)
                        fav.setImageDrawable(getResources().getDrawable(R.drawable.fav_in));
                }
                break;

        }
    }

    @Override
    public void onStartOfSpeech() {

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {

    }

    @Override
    public void onSpeechResult(String result) {
        avi.smoothToHide();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(wordObj.getName().trim().equalsIgnoreCase(result)){
            int k = dataBaseHelper.setComplete(wordObj.getId());
            if(k != 0){
                Cursor c = dataBaseHelper.getAllCmplt();
                cmpltWords = c.getCount();
                count_tv.setText(cmpltWords+"/"+totalWords);
               tick.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            }

        }else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onRecordAudioPermissionGranted();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST);
            }
        }
    }

    private void onRecordAudioPermissionGranted() {
        try {
            avi.smoothToShow();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(WordViewActivity.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            avi.smoothToHide();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            avi.smoothToHide();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showEnableGoogleVoiceTyping();
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(WordViewActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Speech Not Available")
                .setCancelable(false)
                .setPositiveButton("YES", dialogClickListener)
                .setNegativeButton("NO", dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable Voice Typing")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }
}
