package ru.android_cnc.acnc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.android_cnc.acnc.Interpreter.ProgramLoader;


public class MainActivity

        extends
            ActionBarActivity
        implements
            NavigationDrawerFragment.NavigationDrawerCallbacks,
            GcodeTextFragment.OnGcodeEditFragmentInteractionListener,
            GraphicalViewFragment.OnFragmentInteractionListener {

    static ProgramLoader program;

    private static final String MAIN_ACTIVITY = "A CNC MAIN ACTIVITY";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String fileName;
    private String gcodeSource;

    public MainActivity() {
        fileName = "test.cnc";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            gcodeSource = new String(buffer);
            inputStream.close();
        }
		catch (FileNotFoundException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException eio){
            eio.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch(position){
            case 0:
                intentFileOpenDialog();
                break;
            case 1:
                transaction.replace(R.id.container, GcodeTextFragment.newInstance(gcodeSource));
                break;
            case 2:
                transaction.replace(R.id.container, GraphicalViewFragment.newInstance("2","3"));
                break;
            default:
                transaction.replace(R.id.container, PlaceholderFragment.newInstance(position + 1));
        }
        transaction.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.file_open);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    private void intentFileOpenDialog(){
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "cnc" });

        startActivityForResult(intent, FileDialog.RequestType.REQUEST_SAVE.ordinal());
    }

    public synchronized void onActivityResult(final int rc, int resultCode, final Intent data) {

        final FileDialog.RequestType requestCode = FileDialog.RequestType.values()[rc];

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == FileDialog.RequestType.REQUEST_SAVE) {
                System.out.println("Saving...");
            } else if (requestCode == FileDialog.RequestType.REQUEST_LOAD) {
                System.out.println("Loading...");
            }

            String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.w(MAIN_ACTIVITY, "file not selected");
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onGcodeEditFragmentInteraction(String newStr) {
        gcodeSource = newStr;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
