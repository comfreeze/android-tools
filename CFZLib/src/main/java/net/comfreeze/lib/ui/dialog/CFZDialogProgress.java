package net.comfreeze.lib.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.comfreeze.lib.R;

public class CFZDialogProgress extends DialogFragment {
    private static final String TAG = CFZDialogProgress.class.getSimpleName();

    public static final String ARG_TITLE = TAG + ".title";
    public static final String ARG_TYPE = TAG + ".type";

    public static enum Type {
        /* */DEFAULT,
        /* */INDETECFZINATE,
        /* */UPDATING_VALUE
    }

    private Type currentType = Type.DEFAULT;

    private String currentTitle = "TITLE UNSET";

    private int currentStyle = android.R.style.Widget_PopupWindow;
    private int currentLayout = R.layout.cfz_lib_progress_dialog;

    private ProgressBar progressBar;

    private TextView titleView;

    private Button cancelButton;

    public static CFZDialogProgress instance() {
        Log.d(TAG, "Creating instance");
        CFZDialogProgress fragment = new CFZDialogProgress();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "Creating progress dialog");
        View view = LayoutInflater.from(getActivity()).inflate(currentLayout, null, false);
        titleView = (TextView) view.findViewById(R.id.title);
        cancelButton = (Button) view.findViewById(R.id.cancel);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        titleView.setText(currentTitle);
        cancelButton.setOnClickListener(clickListener_cancel);
        progressBar.setIndeterminate(true);
        Dialog dialog = new Dialog(getActivity(), currentStyle);
        dialog.setContentView(view);
        switch (currentType) {
            case INDETECFZINATE:
                dialog.setCancelable(false);
                if (null != cancelButton)
                    cancelButton.setVisibility(View.GONE);
                break;
        }
        return super.onCreateDialog(savedInstanceState);
    }

    public CFZDialogProgress setStyle(int style) {
        this.currentStyle = style;
        return this;
    }

    public CFZDialogProgress setLayout(int layout) {
        this.currentLayout = layout;
        return this;
    }

    public CFZDialogProgress setTitle(String title) {
        this.currentTitle = title;
        return this;
    }

    public CFZDialogProgress setType(Type type) {
        this.currentType = type;
        return this;
    }

    public CFZDialogProgress setType(String type) {
        return setType(Type.valueOf(type));
    }

    public CFZDialogProgress setProgress(int progress) {
        return this;
    }

    private OnClickListener clickListener_cancel = //
            new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                    // TODO: Add listener tie-in here
                }
            };
}