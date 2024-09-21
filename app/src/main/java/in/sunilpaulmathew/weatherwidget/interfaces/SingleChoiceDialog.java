package in.sunilpaulmathew.weatherwidget.interfaces;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 23, 2023
 */
public abstract class SingleChoiceDialog {

    private final int mIcon;
    private final int mPosition;
    private final MaterialAlertDialogBuilder mDialogBuilder;
    private final String mTitle;
    private final String[] mSingleChoiceItems;

    public SingleChoiceDialog(int icon, String title, String[] singleChoiceItems,
                              int position, Context context) {
        this.mIcon = icon;
        this.mTitle = title;
        this.mSingleChoiceItems = singleChoiceItems;
        this.mPosition = position;
        this.mDialogBuilder = new MaterialAlertDialogBuilder(context);
        if (Utils.isAmoledBlackEnabled(context)) {
            this.mDialogBuilder.setBackground(Utils.getDrawable(R.color.color_black, context));
        }
    }

    private void startDialog() {
        if (mIcon > Integer.MIN_VALUE) {
            mDialogBuilder.setIcon(mIcon);
        }
        if (mTitle != null) {
            mDialogBuilder.setTitle(mTitle);
        }
        mDialogBuilder.setSingleChoiceItems(mSingleChoiceItems, mPosition, (dialog, itemPosition) -> {
            onItemSelected(itemPosition);
            dialog.dismiss();
        }).show();
    }

    public void show() {
        startDialog();
    }

    public abstract void onItemSelected(int position);

}