package ru.android_cnc.acnc.GraphView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.android_cnc.acnc.Drivers.Cutter.CutterDriver;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.R;

public class CNCControlViewActivity
        extends ActionBarActivity
        implements
        CNC2DViewFragment.OnGcodeGraphViewFragmentInteractionListener,
        CNCControlFragment.OnCNCControlFragmentInteractionListener{

    private String fileName = null;
    private String sourceText = null;
    private Spannable spannedText = null;
    private ProgramLoader programLoader = null;
    private CutterDriver driver = null;
    private View view2D = null;
    private Fragment cncViewFrag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnccontrol_view);
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
        }else{
            // TODO serve errors
        };

    }

    private boolean prepare(){
        boolean allFine = true;
        Intent intent = getIntent();
        fileName = intent.getStringExtra(getString(R.string.SOURCE_FILE_NAME));
        if(fileName == null) allFine = false;
        else {
            Log.i("File name:", fileName);

            AssetManager assetManager = getAssets();
            try {
                InputStream inputStream = assetManager.open(fileName);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                sourceText = new String(buffer);
                inputStream.close();
            } catch (FileNotFoundException e) {
                allFine = false;
                e.printStackTrace();
            } catch (IOException eio) {
                allFine = false;
                eio.printStackTrace();
            }
        }

        if(allFine){
            try {
                programLoader = new ProgramLoader();
                spannedText = programLoader.load(sourceText);
                programLoader.evalute();
            }
            catch (InterpreterException ie){
                allFine = false;
            };
        }

        if(allFine){
            try {
                driver = new CutterDriver();
                driver.loadProgram(programLoader.command_sequence);
            }
            catch (InterpreterException ie){
                allFine = false;
            };
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
        Log.i("CNC control fragment ", "Start button clicked");
        if(cncViewFrag != null){
            View cncView = ((CNC2DViewFragment)cncViewFrag).getCNCView();
            if(cncView != null)
                if(driver != null)
                    driver.startProgram(cncView);

        }
    }

    public void onStopButtonClick(View v){
        Log.i("CNC control fragment ", "Stop button clicked");
        if(driver != null)driver.pauseProgram();
    }

    public void displayPointCoordinates(CNCPoint point){
        Double t = point.getX();
        TextView fieldX = (TextView)findViewById(R.id.text_value_X);
        fieldX.setText(t.toString());
        t = point.getY();
        TextView fieldY = (TextView)findViewById(R.id.text_value_Y);
        fieldY.setText(t.toString());
    }

}