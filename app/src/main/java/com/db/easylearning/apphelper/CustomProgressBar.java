package com.db.easylearning.apphelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.db.easylearning.R;


public class CustomProgressBar {

    private  Dialog dialog;
    public  void showProgress(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_progress);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public  void hideProgress() {
        if (dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
}
