package ru.android_cnc.acnc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.android_cnc.acnc.FileSelect.FileSelectActivity;
import ru.android_cnc.acnc.GcodeTextEdit.GcodeTextEditActivity;
import ru.android_cnc.acnc.GraphView.CNCControlViewActivity;

public class FourButtonsActivity extends Activity {

    private final static String LOG_TAG = " main activity ->";

    private final static String g_codeFolderName = "samples";
    private String toPathPrefix;
    private String pref_last_file_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_buttons);

        Log.d(LOG_TAG, "Activity created!!!");
        toPathPrefix = getApplicationContext().getFilesDir().getPath();
        Log.d(LOG_TAG, "Application data folder path:" + toPathPrefix);
        pref_last_file_value = toPathPrefix
                             + "/"
                             + g_codeFolderName
                             + "/"
                             + "plast.cnc";
        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS), 0);
//        Log.d(LOG_TAG, "Preferences: " + settings);

        //check for first time run
        boolean firstRun = settings.getBoolean(getString(R.string.PREF_FIRST_RUN_TAG), true);
        Log.d(LOG_TAG, "First run - " + firstRun);
//        firstRun = true; // at debug only
        if ( firstRun ) {
            settings.edit().putBoolean(getString(R.string.PREF_FIRST_RUN_TAG), false).commit(); //set flag to false
            settings.edit().putString(getString(R.string.PREF_LAST_FILE_TAG), pref_last_file_value).commit();
            copyAssetsToDataFolder();
        }

        final Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startControlView();
            }
        });
        final Button openButton = (Button) findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent intent = new Intent(FourButtonsActivity.this, FileSelectActivity.class);
            intent.putExtra(getString(R.string.APP_FOLDER), toPathPrefix);
            startActivityForResult(intent, 1);
            }
        });
        final Button loadButton = (Button) findViewById(R.id.load_button);
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"Load button pressed!", Toast.LENGTH_LONG).show();
            }
        });
        final Button createButton = (Button) findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            String fileName = getSharedPreferences(getString(R.string.PREFS), 0)
                    .getString(getString(R.string.PREF_LAST_FILE_TAG), "");
            startTextEdit(fileName, 0);
            }
        });
    }

    private void startControlView(){
        Intent intent = new Intent(FourButtonsActivity.this, CNCControlViewActivity.class);
        startActivity(intent);
    }

    private void startTextEdit(String fileName, int pos){
        Intent intent = new Intent(FourButtonsActivity.this, GcodeTextEditActivity.class);
        intent.putExtra(getString(R.string.TEXT_FILE_2_EDIT), fileName);
        intent.putExtra(getString(R.string.TEXT_FILE_2_EDIT_POSITION), pos);
        startActivity(intent);
    }

    private void copyAssetsToDataFolder(){
        final String toPath = toPathPrefix
                            + "/"
                            + g_codeFolderName;  // application g-code files folder path
        Log.d(LOG_TAG, "Application data folder path:" + toPath);
        AssetManager assetManager = getAssets();
        if(copyAssetFolder(assetManager, g_codeFolderName, toPath)){
            Toast.makeText(getApplicationContext(),"Files successfully copied!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Could not copy assets files!", Toast.LENGTH_LONG).show();
        };
    }

    private static boolean copyAssetFolder(AssetManager assetManager,
                                           String fromAssetPath, String toPath) {
        Log.d(LOG_TAG, "copyAssetFolder - " + fromAssetPath + " to " + toPath);
        try {
            String[] file_names_list = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file_name : file_names_list)
                if (file_name.contains(".")){
                    Log.d(LOG_TAG, "File - " + file_name);
                    String from = file_name;
                    if(fromAssetPath.length()>0)
                        from = fromAssetPath + "/" + from;
                    res &= copyAsset(assetManager, from, toPath + "/" + file_name);
                }
                else
                    res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file_name,
                            toPath + "/" + file_name);
            Log.d(LOG_TAG, "Result - " + res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        Log.d(LOG_TAG, "copyAsset - " + fromAssetPath + " to " + toPath);
        try {
            InputStream in = assetManager.open(fromAssetPath);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            new File(toPath).createNewFile();
            OutputStream out = new FileOutputStream(toPath);
            out.write(buffer);
            out.flush();
            out.close();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        String new_current_file = data.getStringExtra(getString(R.string.CURRENT_FILE));
//        Log.d(LOG_TAG, "fileName returned - " + new_current_file);
        getSharedPreferences(getString(R.string.PREFS), 0)
                .edit()
                .putString(getString(R.string.PREF_LAST_FILE_TAG), new_current_file)
                .commit();
        startControlView();
    }
}
