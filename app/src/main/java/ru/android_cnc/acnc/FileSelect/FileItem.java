/*
 * Copyright (c) 2015. Alexey Chernysh, Krasnoyarsk, Russia
 * e-mail: ALEXEY DOT CHERNYSH AT GMAIL DOT COM.
 */

package ru.android_cnc.acnc.FileSelect;

import android.util.Log;

import java.io.File;

import ru.android_cnc.acnc.R;

public class FileItem {

    private final static String LOG_TAG = " file item ->";

    private final File file_;
    private boolean up = false;

    public FileItem(File f){ file_ = f; }

    public String getFileName() { return file_.getName();  }
    public int getResourceId() {
        if(isFile())return R.mipmap.ic_file;
        else
            if(up) return R.drawable.ic_action_back;
            else return R.mipmap.ic_folder;
    }
    public boolean isFile() { return file_.isFile(); }

    public static FileItem getUp(String currentPath) {
        Log.d(LOG_TAG, "current path " + currentPath);
        int separatorIndex = currentPath.lastIndexOf("/");
        Log.d(LOG_TAG, "separator position " + separatorIndex);
        if(separatorIndex > 0){
            String upPathName = currentPath.substring(0,separatorIndex);
            Log.d(LOG_TAG, "up path " + upPathName);
            File upPath = new File(upPathName);
            Log.d(LOG_TAG, "up path file" + upPath);
            FileItem upItem = new FileItem(upPath);
            upItem.up = true;
            return upItem;
        }
        return null;
    }

    public File getFile() {
        return file_;
    }
}