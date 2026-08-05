package in.sunilpaulmathew.weatherwidget.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import in.sunilpaulmathew.weatherwidget.BuildConfig;
import in.sunilpaulmathew.weatherwidget.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on August 05, 2026
 */
public class AboutDialog extends BottomSheetDialog {

    @SuppressLint("SetTextI18n")
    public AboutDialog(Context context) {
        super(context);

        View aboutLayout = View.inflate(context, R.layout.layout_about, null);
        MaterialButton mCancelIcon = aboutLayout.findViewById(R.id.cancel_button);
        MaterialTextView mAppTile = aboutLayout.findViewById(R.id.title);
        mAppTile.setText(context.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

        setContentView(aboutLayout);

        mCancelIcon.setOnClickListener(v -> dismiss());

        show();
    }

}