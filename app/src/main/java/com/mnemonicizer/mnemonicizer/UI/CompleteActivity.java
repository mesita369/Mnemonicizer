package com.mnemonicizer.mnemonicizer.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mnemonicizer.mnemonicizer.Adapter.CustomAdapter;
import com.mnemonicizer.mnemonicizer.Adapter.MyCustomListAdapter;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompleteActivity extends AppCompatActivity implements CustomAdapter.AdapterCallback , SpeechDelegate {
    private static final int PERMISSIONS_REQUEST = 1;
    private RecyclerView recyclerView;
    // private ActionMenuView amvMenu;
    public  List<Word> words;
    TextView slect_txt,count_tv;
    ListView alphas;
    private CustomAdapter customAdapter;
    private Button btnnext;
    private Toolbar toolbar;
    private String textCheck;
    private AVLoadingIndicatorView avi;
    private MyCustomListAdapter myAlphaAdapter;
    private NavigationView nv;
    DataBaseHelper helper;
    private int word_id;
    private int adptPosition;
    private List<Word> _words;
    private int totalWords;
    private int cmpltWords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        count_tv = findViewById(R.id.count_tv);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        alphas = findViewById(R.id.alpha_list);
        slect_txt = findViewById(R.id.slct_aplha);
        final List<String> alphabets = Arrays.asList(getResources().getStringArray(R.array.Alphabets));
        myAlphaAdapter = new MyCustomListAdapter(this, alphabets);
        alphas.setAdapter(myAlphaAdapter);
        avi = findViewById(R.id.avi);
        avi.smoothToHide();
        setSupportActionBar(t);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        helper = new DataBaseHelper(this);
        Cursor cursor = helper.getAllCmplt();
        if(cursor.getCount() != 0) {
            words = Word.fromCursor(cursor);
        }else {
            words = new ArrayList<>();
        }
        cmpltWords = cursor.getCount();
        totalWords =  helper.getAllWords().getCount();
        count_tv.setText(cmpltWords+"/"+totalWords);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        customAdapter = new CustomAdapter(this,words,this);
        alphas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slect_txt.setText(alphabets.get(i));
                List<Word> wordList = new ArrayList<>();
                if( i == 0){
                    wordList = words;
                }else{
                    for(int k = 0;k < words.size();k++){
                        if(i > 0){
                            if(words.get(k).getName().charAt(0) == alphabets.get(i).toLowerCase().charAt(0)){
                                wordList.add(words.get(k));
                            }
                        }
                    }
                }

                customAdapter.setFilter(wordList);
                alphas.setVisibility(View.GONE);
            }
        });
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));



        Speech.init(this, getPackageName());

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) findViewById(R.id.srch_view);
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        SearchView.SearchAutoComplete searchAutoComplete = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn).setVisibility(View.GONE);
        searchAutoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        searchAutoComplete.setTextColor(getResources().getColor(android.R.color.white));
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent memory leaks when activity is destroyed
        Speech.getInstance().shutdown();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);
//
//        MenuItem item = menu.findItem(R.id.spinner);
//        MenuItem mSearch = menu.findItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView mSearchView = (SearchView) mSearch.getActionView();
//        mSearchView.setMaxWidth(Integer.MAX_VALUE);
//        mSearchView.setSearchableInfo(searchManager
//                .getSearchableInfo(getComponentName()));
//        mSearchView.setQueryHint("Search");
//        Spinner spinner = (Spinner) item.getActionView();
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);
//
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                customAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                customAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return true;
//    }

    @Override
    public void onPlayCallback( String text) {

        Speech.getInstance().say(text.trim(), new TextToSpeechCallback() {
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
    }

    @Override
    public void onRecCallback(int adapterPosition, List<Word> wordsFiltered, int id, final String text) {
        checkPermission();
        textCheck = text;
        word_id = id;
        adptPosition  = adapterPosition;
        _words = wordsFiltered;
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
            Speech.getInstance().startListening(CompleteActivity.this);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                onRecordAudioPermissionGranted();
            } else {
                // permission denied, boo!
                Toast.makeText(CompleteActivity.this,"Permissino Granted", Toast.LENGTH_LONG).show();
            }
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
        if(textCheck.trim().equalsIgnoreCase(result)){
            int k = helper.setComplete(word_id);
            if(k != 0){
                Cursor c = helper.getAllCmplt();
                cmpltWords = c.getCount();
                count_tv.setText(cmpltWords+"/"+totalWords);
                _words.get(adptPosition).setCmplt_in(1);
                customAdapter.setItems(_words);

            }

        }else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(CompleteActivity.this);
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
    public void toggleList(View v){
        if(alphas.getVisibility() == View.VISIBLE){
            alphas.setVisibility(View.GONE);
        }else{
            alphas.setVisibility(View.VISIBLE);
        }
    }







}
