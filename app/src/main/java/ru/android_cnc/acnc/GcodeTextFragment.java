package ru.android_cnc.acnc;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GcodeTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GcodeTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GcodeTextFragment extends Fragment {
    private static final String SOURCE_TEXT = "G_code_source_text";
    private static final String TEXT_FRAGMENT = "Text fragment event";

    private static SpannableString sourceText = null;

    private OnFragmentInteractionListener mListener;

    public static GcodeTextFragment newInstance(SpannableString st) {
        Log.i(TEXT_FRAGMENT,"New instance");
        GcodeTextFragment fragment = new GcodeTextFragment();
        sourceText = st;
        return fragment;
    }

    public GcodeTextFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_gcode_text, container, false);
        EditText editTextView = (EditText)result.findViewById(R.id.gcode_view_text);
        if(editTextView != null){
            if(sourceText != null)
                editTextView.setText( sourceText, TextView.BufferType.EDITABLE);
            editTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    onButtonPressed(s.toString());
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        return result;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String s) {
        if (mListener != null) {
            mListener.onEditTextFragmentInteraction(s);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onEditTextFragmentInteraction(String newStr);
    }

}
