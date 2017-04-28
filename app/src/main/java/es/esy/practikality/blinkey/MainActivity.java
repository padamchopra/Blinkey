package es.esy.practikality.blinkey;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import es.esy.practikality.blinkey.R;
import android.app.Activity;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ScrollView;
import java.util.*;
import static android.app.Activity.RESULT_OK;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import org.w3c.dom.Text;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener{

    public TextToSpeech t1;
    private TextView txtSpeechInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1= new TextToSpeech(this,this);
        txtSpeechInput = (TextView) findViewById(R.id.stringtospeak);
    }

    @Override
    public void onInit(int t){
        t1.setOnUtteranceCompletedListener(this);
        t1.setLanguage(new Locale("en","IN"));
    }

    @Override
    public void onUtteranceCompleted(String s){
        Toast.makeText(MainActivity.this,"Completed",Toast.LENGTH_SHORT).show();
    }
    public void response(View view){
        EditText hellotext = (EditText) findViewById(R.id.stringtospeak);
        String[] inputUser = hellotext.getText().toString().split(" ");
        for(int i=0;i<inputUser.length;i++){
            switch (inputUser[i]){
                case "add":
                    contentSaver(inputUser,i);
                    i=inputUser.length+1;
                    break;
                case "capture":
                    Intent intent = new Intent(MainActivity.this,OCR.class);
                    startActivity(intent);
                    break;
                case "read":
                    String topic = inputUser[i+1];
                    i = inputUser.length+1;
                    SharedPreferences sharedPref = getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
                    String contentToRead = sharedPref.getString(topic,"Sorry. No content was saved with that title name.");
                    speakitman(contentToRead);
                    DeleteTextBox();
                    break;
                case "help":
                    speakitman("To add a topic. Say add topic, followed by title and then say with content, followed by the content. The title should be of only one word. To listen to your content, say read and then the title");
                    break;
                case "hello":
                    speakitman("Hello there!");
                    break;
            }
        }
    }
    public void speakitman(String tosay){
        HashMap<String,String> stringStringHashMap = new HashMap<String,String>();
        stringStringHashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,tosay);
        t1.speak(tosay,TextToSpeech.QUEUE_FLUSH,stringStringHashMap);
        t1.setSpeechRate(0.8f);
        t1.setPitch(0.9f);
    }
    public void contentSaver(String[] userinp,int i){
        String title = userinp[i+2];
        String content= " ";
        for(int k=i+5;k<userinp.length;k++){
            content = content + userinp[k] + " " ;
        }
        SharedPreferences sharedPref = getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(title,content);
        editor.commit();
        speakitman("Saved");
        DeleteTextBox();
    }
    private void DeleteTextBox(){
        EditText txt = (EditText) findViewById(R.id.stringtospeak);
        txt.setText("");
    }
    public void SpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry, Your device does not support speech recognition.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultString = new String (result.get(0).toString());

                    txtSpeechInput.setText(result.get(0));

                    String[] inputUser = resultString.split(" ");

                    for(int i=0;i<inputUser.length;i++){
                        switch (inputUser[i]){
                            case "add":
                                contentSaver(inputUser,i);
                                i=inputUser.length+1;
                                break;
                            case "capture":
                            case "click":
                            case "photo":
                            case "picture":
                                Intent intent = new Intent(MainActivity.this,OCR.class);
                                startActivity(intent);
                                break;
                            case "read":
                                String topic = inputUser[i+1];
                                i = inputUser.length+1;
                                SharedPreferences sharedPref = getSharedPreferences("sharedpref",Context.MODE_PRIVATE);
                                String contentToRead = sharedPref.getString(topic,"Sorry. No content was saved with that title name.");
                                speakitman(contentToRead);
                                DeleteTextBox();
                                break;
                            case "help":
                                speakitman("To add a topic. Say add topic, followed by title and then say with content, followed by the content. The title should be of only one word. To listen to your content, say read and then the title");
                                break;
                            case "hello":
                                speakitman("Hello there!");
                                break;
                        }
                    }
                }
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}