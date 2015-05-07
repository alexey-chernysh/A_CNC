package ru.android_cnc.acnc.FileSelect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ru.android_cnc.acnc.R;

public class FileSelectActivity
        extends Activity
        implements AdapterView.OnItemClickListener{

    private final static String LOG_TAG = " file dialog ->";

    FileItem[] item = null;
    String currentPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_grid_view);

//      currentPath = "/data/data/" + getApplicationContext().getPackageName();
        currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        prepareGridView();
    }

    private void prepareGridView(){
        File f = new File(currentPath);
        Log.d(LOG_TAG, "file name " + f);
        File[] files = f.listFiles();
        int counter = 0;
        FileItem goUp = FileItem.getUp(currentPath);
        if(goUp != null){
            item = new FileItem[files.length + 1];
            item[0] = goUp;
            counter++;
        } else item = new FileItem[files.length];
        for(File file : files){
            item[counter] = new FileItem(file);
            Log.d(LOG_TAG, "file" + counter + "=" + file);
            counter++;
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(item != null){
            FileItem selectedItem = item[position];
            if(selectedItem.isFile()){ // file selected  - task done
                Intent intent = new Intent();
                intent.putExtra("name", item[position].getFileName());
                setResult(RESULT_OK, intent);
                finish();
            } else { // folder selected - open selected folder
                currentPath = item[position].getFile().getAbsolutePath();
                prepareGridView();
            };
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }
        public int getCount() {
            return item.length;
        }
        public Object getItem(int position) { return item[position]; }
        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View grid;

            if (convertView == null) {
                grid = new View(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                grid = inflater.inflate(R.layout.file_grid_cell, parent, false);
            } else {
                grid = (View) convertView;
            }

            if(item != null){
                ImageView imageView = (ImageView) grid.findViewById(R.id.item_image);
                imageView.setImageResource(item[position].getResourceId());

                TextView textView = (TextView) grid.findViewById(R.id.item_text);
                textView.setText(item[position].getFileName());
            }

            return grid;
        }

    }


}
