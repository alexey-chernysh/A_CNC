package ru.android_cnc.acnc;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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

    private String sourceText;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GcodeTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GcodeTextFragment newInstance(String sourceText) {
        Log.i(TEXT_FRAGMENT,"New instance");
        GcodeTextFragment fragment = new GcodeTextFragment();
        Bundle args = new Bundle();
        args.putString(SOURCE_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public GcodeTextFragment() {
        // Required empty public constructor
        Log.i(TEXT_FRAGMENT,"Public constructor");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TEXT_FRAGMENT,"On create");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sourceText = getArguments().getString(SOURCE_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TEXT_FRAGMENT,"On create view");
        View result = inflater.inflate(R.layout.fragment_gcode_text, container, false);
        EditText editTextView = (EditText)result.findViewById(R.id.gcode_view_text);
        if(editTextView != null){
            editTextView.setText(this.sourceText);
            editTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Toast.makeText(getActivity(), "G-code edited!", Toast.LENGTH_SHORT).show();
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TEXT_FRAGMENT,"On attach");
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
        Log.i(TEXT_FRAGMENT,"On detach");
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
        public void onFragmentInteraction(Uri uri);
    }

}
