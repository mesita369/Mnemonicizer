package com.mnemonicizer.mnemonicizer.UI.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.mnemonicizer.mnemonicizer.Adapter.CustomAdapter;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment implements CustomAdapter.AdapterCallback,SpeechDelegate {


    private static final int PERMISSIONS_REQUEST = 1;
    private DataBaseHelper helper;
    private List<Word> words;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private String textCheck;
    private int word_id;
    private int adptPosition;
    private List<Word> _words;
    private AVLoadingIndicatorView avi;
    private int cmpltWords;

    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_fav, container, false);
        helper = new DataBaseHelper(getActivity());
        avi = v.findViewById(R.id.avi);
        avi.smoothToHide();
        words = new ArrayList<>();
        Cursor cursor = helper.getAllCmplt();
        if(cursor.getCount() != 0){
            words = Word.fromCursor(cursor);
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        customAdapter = new CustomAdapter(getActivity(),words,this);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
       return v;
    }
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
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onRecordAudioPermissionGranted();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST);
            }
        }
    }

    private void onRecordAudioPermissionGranted() {
        try {
            avi.smoothToShow();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(this);

        } catch (SpeechRecognitionNotAvailable exc) {
            avi.smoothToHide();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            avi.smoothToHide();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                Toast.makeText(getActivity(),"Permissino Granted", Toast.LENGTH_LONG).show();
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
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(textCheck.trim().equalsIgnoreCase(result)){
            int k = helper.setComplete(word_id);
//            if(k != 0){
//                Cursor c = helper.getAllCmplt();
//                cmpltWords = c.getCount();
//                count_tv.setText(cmpltWords+"/"+totalWords);
//                _words.get(adptPosition).setCmplt_in(1);
//                customAdapter.setItems(_words);
//
//            }

        }else{
            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(getActivity());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Speech Not Available")
                .setCancelable(false)
                .setPositiveButton("YES", dialogClickListener)
                .setNegativeButton("NO", dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
