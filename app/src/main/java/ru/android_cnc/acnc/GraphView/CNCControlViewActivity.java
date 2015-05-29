package ru.android_cnc.acnc.GraphView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ru.android_cnc.acnc.Drivers.Cutter.CutterDriver;
import ru.android_cnc.acnc.FourButtonsActivity;
import ru.android_cnc.acnc.GcodeTextEdit.GcodeTextEditActivity;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.R;

public class CNCControlViewActivity
        extends ActionBarActivity
        implements
        CNC2DViewFragment.OnGcodeGraphViewFragmentInteractionListener,
        CNCControlFragment.OnCNCControlFragmentInteractionListener{

//    private final static String LOG_TAG = " control view ->";

    private String fileName = null;
    private String sourceText = null;
    private Spannable spannedText = null;
    private ProgramLoader programLoader = null;
    private CutterDriver driver = null;
    private View view2D = null;

    private Fragment cncViewFrag = null;
    private static Context context_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnccontrol_view);
        CNCControlViewActivity.context_ = getApplicationContext();

        if(prepare()){
            if (savedInstanceState == null) {
                cncViewFrag = new CNC2DViewFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_left, cncViewFrag)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_right, new CNCControlFragment())
                        .commit();
            }
        }
    }

    private boolean prepare(){
        boolean allFine = true;

        //open current file for edit
        try {
            fileName = getSharedPreferences(getString(R.string.PREFS), 0)
                      .getString(getString(R.string.PREF_LAST_FILE_TAG), "");
            this.setTitle(fileName);
//            Log.d(LOG_TAG, "Opening file " + fileName);
            InputStream in = new FileInputStream(fileName);
//            Log.d(LOG_TAG, "Characters available: " + in.available());
            if(in != null){
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                sourceText = new String(buffer);
                in.close();
            }
        } catch (IOException e) {
            allFine = false;
            e.printStackTrace();
        }

        if(allFine){
            programLoader = new ProgramLoader();
            spannedText = programLoader.load(sourceText);
            programLoader.evaluate();
        }

        if(allFine){
            try {
                driver = new CutterDriver();
                driver.load(programLoader.command_sequence);
            }
            catch (InterpreterException ie){
                allFine = false;
            }
        }

        return allFine;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cnccontrol_view, menu);
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
    public void onCNCControlFragmentInteraction(Uri uri) {

    }

    @Override
    public void onGcodeGraphViewFragmentInteraction(Uri uri) {

    }

    public void onStartButtonClick(View v){
//        Log.i("CNC control fragment ", "Start button clicked");
        if(cncViewFrag != null){
            View cncView = ((CNC2DViewFragment)cncViewFrag).getCNCView();
            CNCPoint tmpPoint = new CNCPoint(555.0,555.0);
            displayPointCoordinates(tmpPoint);
            if(cncView != null)
                if(driver != null)
                    driver.start(cncView);

        }
    }

    public void onStopButtonClick(View v){
//        Log.i("CNC control fragment ", "Stop button clicked");
        CNCPoint tmpPoint = new CNCPoint(111.0,111.0);
        displayPointCoordinates(tmpPoint);
        if(driver != null)
            driver.pause();
    }

    public void displayPointCoordinates(CNCPoint point){
        Double t;
        t = point.getX();
        TextView fieldX = (TextView)findViewById(R.id.text_value_X);
        if(fieldX != null) fieldX.setText(t.toString());
        t = point.getY();
        TextView fieldY = (TextView)findViewById(R.id.text_value_Y);
        if(fieldY != null) fieldY.setText(t.toString());
    }

    public static Context getAppContext() {
        return CNCControlViewActivity.context_;
    }
}
