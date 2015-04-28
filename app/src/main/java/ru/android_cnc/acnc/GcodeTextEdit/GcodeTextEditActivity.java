package ru.android_cnc.acnc.GcodeTextEdit;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.android_cnc.acnc.FourButtonsActivity;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.R;

public class GcodeTextEditActivity extends ActionBarActivity {

    private final static String LOG_TAG = " edit text ->";

    private String fileName = null;
    private String title = null;
    private String sourceText = null;
    private Spannable spannedText = null;
    private ProgramLoader programLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcode_text_edit);
    }

    @Override
    public void onStart(){
        super.onStart();

        //open current file for edit
        try {
            title = getSharedPreferences(FourButtonsActivity.pref_name, 0)
                   .getString(FourButtonsActivity.pref_last_file_tag, "");
            fileName = FourButtonsActivity.toPathPrefix
                    + getPackageName()
                    + "/"
                    + FourButtonsActivity.g_codeFolderName
                    + "/"
                    + title;
            this.setTitle(title);
            Log.d(LOG_TAG, "Opening file " + fileName);
            InputStream in = new FileInputStream(fileName);
            Log.d(LOG_TAG, "Characters available: " + in.available());
            if(in != null){
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                in.close();
                sourceText = new String(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int cursorPos = 0;

        programLoader = new ProgramLoader();
        spannedText = programLoader.load(sourceText);

        EditText te = (EditText)findViewById(R.id.edit_text);
        te.setText(spannedText);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gcode_text_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        if(fileName != null){
            EditText te = (EditText)findViewById(R.id.edit_text);
            if(te != null){
                Editable text = te.getText();
                byte[] buffer = text.toString().getBytes();
                try {
                    new File(fileName).createNewFile();
                    OutputStream out = new FileOutputStream(fileName);
                    out.write(buffer);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onStop();
    }
}
