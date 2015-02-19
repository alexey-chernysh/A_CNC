package ru.android_cnc.acnc.Interpreter;

import android.text.SpannableString;

/**
 * @author Alexey Chernysh
 */

public class SourceString {

    private String source_;
    private SpannableString colored;

    public SourceString(String s){
        source_ = s;
        colored = new SpannableString(source_);
    }

}
