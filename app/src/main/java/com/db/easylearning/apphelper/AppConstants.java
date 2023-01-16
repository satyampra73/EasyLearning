package com.db.easylearning.apphelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.db.easylearning.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppConstants {
    private static Dialog dialog;
    public static final String SPEAKIFY_API_KEY = "EASYLEARNING24-API-KEY";
    public static final String SPEAKIFYKEYVALUE = "195c40a3b97e56fe4c699da5d88b5b0bdf3823d5";
//    //rewarded Test
//    public static final String AddUnitId ="ca-app-pub-3940256099942544/5224354917";

    //    rewarded video real
    public static final String AddUnitId = "ca-app-pub-3940256099942544/5224354917";


    private RewardedAd rewardedAd;
    boolean isLoading;

    public AppConstants(RewardedAd rewardedAd, boolean isLoading) {
        this.rewardedAd = rewardedAd;
        this.isLoading = isLoading;
    }

    public AppConstants(Dialog dialog) {
        AppConstants.dialog = dialog;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void openDialog(String text, Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progress_dialog);
        TextView txtProgressDialog = dialog.findViewById(R.id.txtProgressDialog);
        txtProgressDialog.setText(text);

        dialog.show();
    }

    public void closeDialog() {
        dialog.dismiss();
    }

    public void loadRewardedAd(Context context, Activity activity) {

        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context,
                    AppConstants.AddUnitId,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.d("rewarded", "video loading error " + loadAdError.getMessage());
                            rewardedAd = null;
                            AppConstants.this.isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);

                            AppConstants.this.rewardedAd = rewardedAd;
                            Log.d("PlanC", "onAdLoaded");
                            AppConstants.this.isLoading = false;

                            showRewardedVideo(context, activity);
                        }
                    });
        }
    }

    private void showRewardedVideo(Context context, Activity activity) {
        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("PlanC", "onAdShowedFullScreenContent");

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("PlanC", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;
                        Log.d("PlanC", "onAdDismissedFullScreenContent");

                        // Preload the next rewarded ad.
                    }
                });
        Activity activityContext = activity;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }


    public static String getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String str = sdf.format(new Date());
        return str;
    }

}
