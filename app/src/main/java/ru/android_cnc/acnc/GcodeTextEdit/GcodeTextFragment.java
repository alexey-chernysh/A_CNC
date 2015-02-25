package ru.android_cnc.acnc.GcodeTextEdit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GcodeTextFragment.OnGcodeEditFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GcodeTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GcodeTextFragment extends Fragment {

    private static final String TEXT_FRAGMENT = "Text fragment event";
    private static String sourceText = null;
    private OnGcodeEditFragmentInteractionListener mListener;

    public static GcodeTextFragment newInstance(Context context, String st) {
        GcodeTextFragment fragment = new GcodeTextFragment();
        sourceText = st;
        Bundle args = new Bundle();
        String argumentTag = context.getString(R.string.SOURCE_TEXT);
        Log.i(TEXT_FRAGMENT,"Argument tag =" + argumentTag);
        args.putString(argumentTag, sourceText);
        fragment.setArguments(args);
        return fragment;
    }

    public GcodeTextFragment() {
    }

    private void setViewText(View vw){
        if(vw != null){
            EditText editTextView = (EditText)vw.findViewById(R.id.gcode_view_text);
            if(editTextView != null){
                if (getArguments() != null) {
                    String argumentTag = getString(R.string.SOURCE_TEXT);
                    Log.i(TEXT_FRAGMENT,"Argument tag =" + argumentTag);
                    sourceText = getArguments().getString(argumentTag);
                };
                if(sourceText != null){
                    Spannable wordToSpan = new SpannableString(sourceText);
                    try{
                        wordToSpan = ProgramLoader.load(sourceText);
                    }catch (InterpreterException ie){
                        Toast.makeText(getActivity(), ie.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    editTextView.setText( wordToSpan, TextView.BufferType.EDITABLE);
                };
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewText(this.getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_gcode_text, container, false);
        EditText editTextView = (EditText)result.findViewById(R.id.gcode_view_text);
        if(editTextView != null){
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
        setViewText(editTextView);
        return result;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String s) {
        if (mListener != null) {
            String argumentTag = getString(R.string.SOURCE_TEXT);
            Log.i(TEXT_FRAGMENT,"Argument tag =" + argumentTag);
            getArguments().putString(argumentTag, s);
            mListener.onGcodeEditFragmentInteraction(s);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGcodeEditFragmentInteractionListener) activity;
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
    public interface OnGcodeEditFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onGcodeEditFragmentInteraction(String newStr);
    }

}
