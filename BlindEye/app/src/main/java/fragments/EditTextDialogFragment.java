package fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;

import com.mad1.blindeye.R;

import utils.ViewUtil;

//DialogFragment that'll ask the user to confirm EditText
public class EditTextDialogFragment extends DialogFragment {
    //key for requesting code
    //will be passed in callbacks
    private static final String REQUEST_CODE_ARG = "EditTextDialogFragment.Args.REQUEST_CODE_ARC";

    //key for passing resource id of title
    private static final String TITLE_RESOURCE_ID = "EditTextDialogFragment.Args.TITLE_RESOURCE_ID";

    //key for passing resource id of positive button
    private static final String POSITIVE_BUTTON_ID = "EditTextDialogFragment.Args.POSITIVE_BUTTON_ID";

    //key for passing resource id of negative button
    private static final String NEGATIVE_BUTTON_ID = "EditTextDialogFragment.Args.NEGATIVE_BUTTON_ID";

    //key for passing resource id of hint text
    private static final String HINT_TEXT_ARG = "EditTextDialogFragment.Args.HINT_TEXT_ARG";

    //key for passing initial text value
    private static final String INITIAL_TEXT_ARG = "EditTextDialogFragment.Args.INITIAL_TEXT_ARG";

    //key for allowing an empty string
    private static final String EMPTY_TEXT_ARG = "EditTextDialogFragment.Args.EMPTY_TEXT_ARG";

    //new instance of EditTextDialogFragment that'll ask user to name the palette
    public static EditTextDialogFragment newFragmentInstance(int reqCode, @StringRes int titleResID, @StringRes int posButtonResID,
                                                             @StringRes int negButtonResID, String textEditHint, String textEditInitial) {
        return newFragmentInstance(reqCode, titleResID, posButtonResID, negButtonResID, textEditHint, textEditInitial, false);
    }

    //new instance of EditTextDialogFragment that'll ask user to name the palette
    public static EditTextDialogFragment newFragmentInstance(int reqCode, @StringRes int titleResID, @StringRes int posButtonResID,
                                                             @StringRes int negButtonResID, String textEditHint, String textEditInitial, boolean emptyString) {
        final EditTextDialogFragment editTextDialogFragment = new EditTextDialogFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(REQUEST_CODE_ARG, reqCode);
        bundle.putInt(TITLE_RESOURCE_ID, titleResID);
        bundle.putInt(POSITIVE_BUTTON_ID, posButtonResID);
        bundle.putInt(NEGATIVE_BUTTON_ID, negButtonResID);
        bundle.putString(HINT_TEXT_ARG, textEditHint);
        bundle.putString(INITIAL_TEXT_ARG, textEditInitial);
        bundle.putBoolean(EMPTY_TEXT_ARG, emptyString);
        editTextDialogFragment.setArguments(bundle);
        return editTextDialogFragment;
    }

    //a callback that'll be notified for EditTextDialogFragment
    private Callback mCallback;

    //EditText for the actual edited text
    private EditText mEditText;

    //ObjectAnimator for playing a 'no' animation when user attempts to validate an empty string, and boolean emptyString is false
    private ObjectAnimator mNoAnim;

    //request code
    private int mReqCode;

    //boolean that allows users to validate empty strings if boolean is true
    private boolean mEmptyString;

    //default constructor
    //every fragment must have a default constructor to be instantiated when restoring the state of an activity
    public EditTextDialogFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        } else {
            throw new IllegalStateException("Activity has to implement callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        final Bundle args = getArguments();

        //ensure args are present
        ensureArgsArePresent(args);

        //extracting the args
        mReqCode = args.getInt(REQUEST_CODE_ARG);
        mEmptyString = args.getBoolean(EMPTY_TEXT_ARG);
        final int titleResID = args.getInt(TITLE_RESOURCE_ID);
        final int posButtonResID = args.getInt(POSITIVE_BUTTON_ID);
        final int negButtonResID = args.getInt(NEGATIVE_BUTTON_ID);
        final String textEditHint = args.getString(HINT_TEXT_ARG);
        final String textEditInitial = args.getString(INITIAL_TEXT_ARG);

        final Context context = getActivity();
        final View view = LayoutInflater.from(context).inflate(R.layout.edit_text_dialog_fragment, null);

        mEditText = (EditText) view.findViewById(R.id.edit_text_dialog_fragment_edit);
        mEditText.setHint(textEditHint);
        mEditText.setText(textEditInitial);
        mNoAnim = ViewUtil.noAnim(mEditText, mEditText.getPaddingLeft());

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view).setTitle(titleResID).setCancelable(true)
                //don't want positive button to dismiss the alert dialog all the time
                //the onClickListener is set in an OnShowListener below this
                .setPositiveButton(posButtonResID, null)
                .setNegativeButton(negButtonResID, (dialog, which) -> handleNegBtnClick());

        //setting positive button onClickListener
        final AlertDialog dialog = builder.create();
        //onShowListener referenced on line 137
        dialog.setOnShowListener(dialogInterface -> {
            final Button posBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            posBtn.setOnClickListener(v -> handlePosBtnClick());
        });

        //user presses the IME action done, it is considered a positive click
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handlePosBtnClick();
                return true;
            }
            return false;
        });

        return dialog;
    }

    //handing the click of the green tick when user clicks it
    private void handlePosBtnClick() {
        final String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text) && !mEmptyString) {
            if (mNoAnim.isRunning()) {
                mNoAnim.cancel();
            }
            mNoAnim.start();
        } else {
            mCallback.onEditPositiveButtonClicked(mReqCode, text);
            getDialog().dismiss();
        }
    }

    //handing the click of the red x when user clicks it
    private void handleNegBtnClick() {
        mCallback.onEditNegativeButtonClicked(mReqCode);
    }

    //checking if the required args are present in the Bundle above. Line 112 onwards a little.
    private void ensureArgsArePresent(Bundle args) {
        if (args == null) {
            throw new IllegalArgumentException("Args cannot be null");
        }
        if (!args.containsKey(REQUEST_CODE_ARG)) {
            throw new IllegalArgumentException("No req code, please use newFragmentInstance()");
        }
        if (!args.containsKey(TITLE_RESOURCE_ID)) {
            throw new IllegalArgumentException("No title resource ID, please use newFragmentInstance()");
        }
        if (!args.containsKey(POSITIVE_BUTTON_ID)) {
            throw new IllegalArgumentException("No positive button ID, please use newFragmentInstance()");
        }
        if (!args.containsKey(NEGATIVE_BUTTON_ID)) {
            throw new IllegalArgumentException("No negative button ID, please use newFragmentInstance()");
        }
        if (!args.containsKey(HINT_TEXT_ARG)) {
            throw new IllegalArgumentException("Missing hint text resource ID, please use newFragmentInstance()");
        }
        if (!args.containsKey(INITIAL_TEXT_ARG)) {
            throw new IllegalArgumentException("Missing initial text, please use newFragmentInstance()");
        }
        if (!args.containsKey(EMPTY_TEXT_ARG)) {
            throw new IllegalArgumentException("Missing empty string, please use newFragmentInstance()");
        }
    }

    //interface for EditTextDialogFragment callbacks
    public interface Callback {
        //called when user clicks positive button
        void onEditPositiveButtonClicked(int reqCode, String text);

        //called when user clicks negative button
        void onEditNegativeButtonClicked(int reqCode);
    }
}
