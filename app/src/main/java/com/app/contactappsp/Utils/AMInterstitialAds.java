package com.app.contactappsp.Utils;

import android.content.Context;

import com.app.contactappsp.Utils.interfacess.onInterstitialAdsClose;
import com.google.android.gms.ads.InterstitialAd;

public class AMInterstitialAds {
    public Context context;
    public InterstitialAd interstitialAd;

    private static onInterstitialAdsClose adsListener1;

    public AMInterstitialAds(Context context, boolean isreload) {
        this.context = context;
        loadAMInterstitialAd(isreload);
    }

    public void loadAMInterstitialAd(final boolean isreload) {
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(Constants.AM_INTERSTITIAL_ID);
        interstitialAd.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
        interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                adsListener1.onAdClosed();
                // Code to be executed when the interstitial ad is closed.
                if (isreload)
                    interstitialAd.loadAd(new com.google.android.gms.ads.AdRequest.Builder().build());
            }
        });
    }

    public boolean isLoaded() {
        return interstitialAd.isLoaded();
    }

    public void showFullScreenAds(onInterstitialAdsClose oninterstitialadsclose) {

        adsListener1 = oninterstitialadsclose;

        if (interstitialAd != null) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }
    }




}
