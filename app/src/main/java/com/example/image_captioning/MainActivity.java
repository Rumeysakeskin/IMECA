package com.example.image_captioning;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;

import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.apache.commons.collections4.functors.NonePredicate;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity<original, tts> extends AppCompatActivity {
    ImageView picture;
    Animation frombottom;
    Animation menu_anim;
    LinearLayout explore, menus;
    private TextToSpeech tts;
    private TextToSpeech tts_custom;
    private Button first;
    private Button second;
    private Button generate_button;
   // private Button speaker;
    private ImageButton speak_button; //microphone
    private TextView caption_text;
    private Button speaker;
    private byte[] data;
    private String cap_original;
    private String cap_custom;
    private TextView gallery_text, camera_text;

    private static boolean original = true;
    private static int speed_value = 50;
    private static int pitch_value = 50;
    private static int selectedLanguage = 1;
    
    private static Bitmap bitmap;
    ImageView imageView;
    Uri imageUri;
    float x1, x2, y1, y2;

    public static final String TAG = "TAG";

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int IMAGE_CAMERA_CODE = 1001;
    private static final int MICROPHONE_CODE = 1002;
    private static final int GENERATE_CAPTION = 1003;
    static final int SELECT_IMAGE = 12;

//    private static final String MODEL_FILE = "file:///android_asset/optimized_model_80_epoch.pb";
    private static final String MODEL_FILE = "file:///android_asset/merged_23_may.pb";
    private static final String INPUT1 = "encoder/import/input_1:0";
//    private static final String INPUT1 = "encoder/import/input:0";
//   private static final String INPUT1 = "encoder/import/InputImage:0";

    private static final String OUTPUT_NODES = "DecoderOutputs.txt";
    private TensorFlowInferenceInterface InferenceInterface;

    private static final int[] DIM_IMAGE = new int[]{1, 299, 299, 3};
    private String[] OutputNodes = null;
    private String[] WORD_MAP = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        speak_button = findViewById(R.id.mic);
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        generate_button = findViewById(R.id.generate_button);
        speaker = findViewById(R.id.speaker);
        caption_text = findViewById(R.id.caption_text);
//        speaker = findViewById(R.id.speaker);
        LottieAnimationView animationView = findViewById(R.id.upload_lottie);
        LottieAnimationView swipeLeft = findViewById(R.id.swipe_left);
        LottieAnimationView swipeRight = findViewById(R.id.swipe_right);
        gallery_text = findViewById(R.id.gallery_text);
        camera_text =findViewById(R.id.camera_text);

        animationView.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Locale pLang = Locale.getDefault();

    //    speaker.setVisibility(View.VISIBLE);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        first.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        tts_custom = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts_custom.setLanguage(pLang);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        first.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        //PERMISSION
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,
                CAMERA, READ_CONTACTS, INTERNET, VIBRATE, VIBRATOR_SERVICE}, PackageManager.PERMISSION_GRANTED);

        speak_button.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    voice_input();
                    animationView.setVisibility(View.INVISIBLE);
                    swipeLeft.setVisibility(View.INVISIBLE);
                    swipeRight.setVisibility(View.INVISIBLE);
                    gallery_text.setVisibility(View.INVISIBLE);
                    camera_text.setVisibility(View.INVISIBLE);
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    microphone();
                    return super.onSingleTapUp(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        first.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    open_gallery();
                    animationView.setVisibility(View.INVISIBLE);
                    swipeLeft.setVisibility(View.INVISIBLE);
                    swipeRight.setVisibility(View.INVISIBLE);
                    gallery_text.setVisibility(View.INVISIBLE);
                    camera_text.setVisibility(View.INVISIBLE);
                    return super.onDoubleTap(e);

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    gallery();
                    return super.onSingleTapUp(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        second.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    animationView.setVisibility(View.INVISIBLE);
                    swipeLeft.setVisibility(View.INVISIBLE);
                    swipeRight.setVisibility(View.INVISIBLE);
                    gallery_text.setVisibility(View.INVISIBLE);
                    camera_text.setVisibility(View.INVISIBLE);
                    open_camera();
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    camera();
                    return super.onSingleTapUp(e);
                }
            });

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
        generate_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                caption_text.setVisibility(View.VISIBLE);
                generate_button.setVisibility(View.INVISIBLE);
          //      animationView.setVisibility(View.VISIBLE);
                translateTextToLanguage();
                getSelectedLanguage(selectedLanguage);
                if(selectedLanguage == 41){tts_custom.speak("Altyazı üretiliyor", TextToSpeech.QUEUE_FLUSH, null);}
                else{tts.speak("Caption is generating", TextToSpeech.QUEUE_FLUSH, null);}
                InferenceInterface = InitSession();
                final String text = runModel(bitmap);

                getSelectedLanguage(selectedLanguage);
                if(selectedLanguage == 41){tts_custom.speak("Altyazıyı dinlemek için hoparlöre basınız", TextToSpeech.QUEUE_FLUSH, null);}
                else{tts.speak("Please tap the speaker to listen to caption", TextToSpeech.QUEUE_FLUSH, null);}
                caption_text.setText(text);
                generate_button.setVisibility(View.VISIBLE);
            }
        });



//        speaker.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tts.speak(caption_text.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });

        //animation
        picture = (ImageView) findViewById(R.id.picture);
 //       frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
  //      menu_anim = AnimationUtils.loadAnimation(this, R.anim.menu_anim);
 //       picture.animate().translationY(-1600).setDuration(1000).setStartDelay(1000);
        //explore = (LinearLayout) findViewById(R.id.explore);
        menus = (LinearLayout) findViewById(R.id.menus);
   //     explore.startAnimation(frombottom);
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    Intent i = new Intent(MainActivity.this, SwipeLeft.class);
                    open_gallery();

                } else if (x1 > x2) {
                    Intent i = new Intent(MainActivity.this, SwipeRight.class);
//                startActivity(i);
                    open_camera();
                }
                break;

        }
        return false;
    }

    public static boolean getLanguage() {
        return original;
    }

    public static void setLanguage(boolean state) {
        original = state;
    }

    public static int getSpeechSpeed() {
        Log.d("DEBUG", String.valueOf(speed_value));
        return speed_value;
    }

    public static void setSpeechSpeed(int state) {
        speed_value = state;
    }

    public static int getSpeechPitch() {
        Log.d("DEBUG", String.valueOf(pitch_value));
        return pitch_value;
    }

    public static void setSpeechPitch(int state) {
        pitch_value = state;
    }

    public static int getSelectedLanguage(int selectedLanguage) {
        Log.d("DEBUG", "LANGUAGE IS SELECTED");
        Log.d("DEBUG", String.valueOf(selectedLanguage));
        return selectedLanguage;
    }

    public static void setSelectedLanguage(int state) {
        selectedLanguage = state;
    }

    private void getLangCode(){
        String langCode = null;
        getSelectedLanguage(selectedLanguage);
        if(selectedLanguage == 1){langCode = "en";}
        if(selectedLanguage == 2){langCode = "af";}
        if(selectedLanguage == 3){langCode = "ar";}
        if(selectedLanguage == 4){langCode = "az";}
        if(selectedLanguage == 5){langCode = "bg";}
        if(selectedLanguage == 6){langCode = "ca";}
        if(selectedLanguage == 7){langCode = "zh";}
        if(selectedLanguage == 8){langCode = "hr";}
        if(selectedLanguage == 9){langCode = "cs";}
        if(selectedLanguage == 10){langCode = "da";}
        if(selectedLanguage == 11){langCode = "nl";}
        if(selectedLanguage == 12){langCode = "et";}
        if(selectedLanguage == 13){langCode = "fi";}
        if(selectedLanguage == 14){langCode = "fr";}
        if(selectedLanguage == 15){langCode = "gl";}
        if(selectedLanguage == 16){langCode = "ka";}
        if(selectedLanguage == 17){langCode = "de";}
        if(selectedLanguage == 18){langCode = "el";}
        if(selectedLanguage == 19){langCode = "hi";}
        if(selectedLanguage == 20){langCode = "hu";}
        if(selectedLanguage == 21){langCode = "is";}
        if(selectedLanguage == 22){langCode = "id";}
        if(selectedLanguage == 23){langCode = "it";}
        if(selectedLanguage == 24){langCode = "ja";}
        if(selectedLanguage == 25){langCode = "ko";}
        if(selectedLanguage == 26){langCode = "lv";}
        if(selectedLanguage == 27){langCode = "lt";}
        if(selectedLanguage == 28){langCode = "ms";}
        if(selectedLanguage == 29){langCode = "no";}
        if(selectedLanguage == 30){langCode = "fa";}
        if(selectedLanguage == 31){langCode = "pl";}
        if(selectedLanguage == 32){langCode = "pt";}
        if(selectedLanguage == 33){langCode = "ro";}
        if(selectedLanguage == 34){langCode = "ru";}
        if(selectedLanguage == 35){langCode = "sr";}
        if(selectedLanguage == 36){langCode = "sk";}
        if(selectedLanguage == 37){langCode = "sl";}
        if(selectedLanguage == 38){langCode = "es";}
        if(selectedLanguage == 39){langCode = "sv";}
        if(selectedLanguage == 40){langCode = "th";}
        if(selectedLanguage == 41){langCode = "tr";}
        if(selectedLanguage == 42){langCode = "uk";}
        if(selectedLanguage == 43){langCode = "vi";}
        if(selectedLanguage == 44){langCode = "cy";}
        translateText(langCode);

    }

    private void translateText(String langCode){

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
//                //to language
                .setTargetLanguage(langCode)
                .build();

       final Translator translator = Translation.getClient(options);

        Log.d("DEBUG",caption_text.getText().toString());
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Log.d("translator", "downloaded lang  model");
                                translator.translate(caption_text.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        caption_text.setText(translatedText);

                                        speaker.setOnClickListener(new Button.OnClickListener() {
                                            public void onClick(View v) {
                                                tts_custom.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null);

                                            }
                                        });
                                        Log.d("translator", translatedText);

                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("translator", "ERROR");

                                            }
                                        });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("translator", "FAILURE");
                            }
                        });
    }

    public void translateTextToLanguage() {
                //First identify the language of the entered text
        FirebaseLanguageIdentification languageIdentifier =
                FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        languageIdentifier.identifyLanguage(caption_text.getText().toString())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    Log.d("translator", "lang " + languageCode);
                                    //download translator for the identified language
                                    // and translate the entered text into english
                                    getLangCode();

                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Could not identify language of the text entered",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,
                                        "Problem in identifying language of the text entered",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
           }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_preference:
                goToPreference();
                return true;
//            case R.id.logOut:
//                startActivity(new Intent(MainActivity.this, MainActivity.class));
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void goToPreference(){
        Intent intent = new Intent(this,Preference.class);
        tts.speak("Settings is opened", TextToSpeech.QUEUE_FLUSH, null);
        startActivity(intent);
    }
    String[] LoadFile(String fileName){
        InputStream is = null;
        try {
            is = this.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString().split("\n");

    }

    TensorFlowInferenceInterface InitSession(){
        InferenceInterface = new TensorFlowInferenceInterface(this.getAssets(), MODEL_FILE);
        OutputNodes = LoadFile(OUTPUT_NODES);
        WORD_MAP = LoadFile("Idmap_23_may");
        Log.d("DEBUG","INIT SESSION");
        return InferenceInterface;

    }
    String runModel(Bitmap bitmap){
        return GenerateCaptions(Preprocess(bitmap));
    }

    float[] Preprocess(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, true);

        int[] intValues = new int[299 * 299];
        float[] floatValues = new float[299 * 299 * 3];

        bitmap.getPixels(intValues, 0, 299, 0, 0, 299, 299);

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = ((float) ((val >> 16) & 0xFF)) / 255;//R
            floatValues[i * 3 + 1] = ((float) ((val >> 8) & 0xFF)) / 255;//G
            floatValues[i * 3 + 2] = ((float) ((val & 0xFF))) / 255;//B
        }

        return floatValues;
    }

    String GenerateCaptions(float[] imRGBMatrix){
        long startTime = SystemClock.currentThreadTimeMillis();
        InferenceInterface.feed(INPUT1, imRGBMatrix, 1, 299,299, 3);
        InferenceInterface.run(OutputNodes);


        String result = "";
        int temp[][]= new int[22][1];
        for(int i = 0; i<22; ++i) {
            InferenceInterface.fetch(OutputNodes[i], temp[i]);

            if(temp[i][0] == 2/*</S>*/){
                long costTime = SystemClock.currentThreadTimeMillis() - startTime;
                Log.i("GenerateCaptions", "GenerateCaptions end, cost time=" + costTime + "ms");
                return result;
            }

            result += WORD_MAP[temp[i][0]]+" ";
        }
        return null;
    }

    private void gallery() {

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500); //1000 yazarsak 1 saniyelik titreşim yaratır
        getSpeechSpeed();
        float speed =speed_value/50;
        if(speed<0.1) speed = 0.1f;
        tts.setSpeechRate(speed);
        tts_custom.setSpeechRate(speed);
        getSpeechPitch();
        float pitch = (float) pitch_value/50;
        if(pitch<0.1) pitch = 0.1f;
        tts.setPitch(pitch);
        tts_custom.setPitch(pitch);
        if (MainActivity.getLanguage()){
            getSelectedLanguage(selectedLanguage);
            if(selectedLanguage == 41){tts_custom.speak("galeri", TextToSpeech.QUEUE_FLUSH, null);}
            else{tts.speak("gallery", TextToSpeech.QUEUE_FLUSH, null); }} }
    private void camera() {
        getSpeechSpeed();
        float speed =speed_value/50;
        if(speed<0.1) speed = 0.1f;
        tts.setSpeechRate(speed);
        tts_custom.setSpeechRate(speed);
        getSpeechPitch();
        float pitch = (float) pitch_value/50;
        if(pitch<0.1) pitch = 0.1f;
        tts.setPitch(pitch);
        tts_custom.setPitch(pitch);
        if (MainActivity.getLanguage()){
            getSelectedLanguage(selectedLanguage);
            if(selectedLanguage == 41){tts_custom.speak("kamera", TextToSpeech.QUEUE_FLUSH, null);}
            tts.speak("camera", TextToSpeech.QUEUE_FLUSH, null); }}
    private void microphone() {
        getSpeechSpeed();
        float speed =speed_value/50;
        if(speed<0.1) speed = 0.1f;
        tts.setSpeechRate(speed);
        tts_custom.setSpeechRate(speed);
        getSpeechPitch();
        float pitch = (float) pitch_value/50;
        if(pitch<0.1) pitch = 0.1f;
        tts.setPitch(pitch);
        tts_custom.setPitch(pitch);
        if (MainActivity.getLanguage()){
            getSelectedLanguage(selectedLanguage);
            if(selectedLanguage == 41){tts_custom.speak("mikrofon", TextToSpeech.QUEUE_FLUSH, null);}
            else{tts.speak("microphone", TextToSpeech.QUEUE_FLUSH, null); } }}

    private void open_gallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (MainActivity.getLanguage()){
            getSelectedLanguage(selectedLanguage);
            if(selectedLanguage == 41){tts_custom.speak("Galeri açıldı", TextToSpeech.QUEUE_FLUSH, null);}
           else{tts.speak("Gallery is opened", TextToSpeech.QUEUE_FLUSH, null); }}
        caption_text.setVisibility(View.INVISIBLE);
        startActivityForResult(intent, SELECT_IMAGE);
    }
    private void open_camera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getSelectedLanguage(selectedLanguage);
        if(selectedLanguage == 41){tts_custom.speak("Kamera açıldı", TextToSpeech.QUEUE_FLUSH, null);}
        else{tts.speak("Camera is opened",TextToSpeech.QUEUE_FLUSH, null);}
        caption_text.setVisibility(View.INVISIBLE);
        startActivityForResult(camera,IMAGE_CAMERA_CODE);
    }
    private void voice_input(){
        Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        getSelectedLanguage(selectedLanguage);
        if(selectedLanguage == 41){tts_custom.speak("Size nasıl yardım edebilirim", TextToSpeech.QUEUE_FLUSH, null);
                                    voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Size nasıl yardım edebilirim?");}
        else{voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "How can I help you?");
        tts.speak("How can I help you?",TextToSpeech.QUEUE_FLUSH, null);}
        startActivityForResult(voice, MICROPHONE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MICROPHONE_CODE && resultCode == RESULT_OK) {  //Voice Input
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result.get(0).toString().contains("camera")) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tts.speak("Camera is opened", TextToSpeech.QUEUE_FLUSH, null);
                startActivityForResult(camera, IMAGE_CAMERA_CODE);

            }

            if (result.get(0).toString().contains("settings")) {
                Intent intent = new Intent(this, MainActivity2.class);
                tts.speak("Settings is opened", TextToSpeech.QUEUE_FLUSH, null);
                startActivity(intent);
            }

            if (result.get(0).toString().contains("gallery")) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                tts.speak("gallery is opened", TextToSpeech.QUEUE_FLUSH, null);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        }

        if (requestCode == IMAGE_CAMERA_CODE && resultCode == RESULT_OK) {
            bitmap =(Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

        }

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imageView.setImageBitmap(bitmap);
                getSelectedLanguage(selectedLanguage);
                if(selectedLanguage == 41){tts_custom.speak("Görsel yüklendi", TextToSpeech.QUEUE_FLUSH, null);}
                else{tts.speak("Image is uploaded", TextToSpeech.QUEUE_FLUSH, null);}


            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {    // CTRL + O
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

