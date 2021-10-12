package com.ldt.musicr.ui.page.settingpage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.ldt.musicr.App;
import com.ldt.musicr.R;
import com.ldt.musicr.helper.LocaleHelper;
import com.ldt.musicr.ui.widget.rangeseekbar.OnRangeChangedListener;
import com.ldt.musicr.ui.widget.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity implements OnRangeChangedListener {

    private static final String EN = "en";
    private static final String VI = "vi";

    @BindView(R.id.status_bar)
    View mStatusBar;

    @BindView(R.id.switch_to_vi)
    TextView mSwitchToVi;

    @BindView(R.id.switch_to_en)
    TextView mSwitchToEn;

    @BindView(R.id.in_app_volume_seek_bar)
    RangeSeekBar mAppVolumeSeekBar;

    @BindView(R.id.left_right_balance_seek_bar)
    RangeSeekBar mBalanceSeekBar;

    @BindView(R.id.hide_switch)
    SwitchCompat mUseArtistImgAsBg;

    @BindView(R.id.create_now)
    View mCreateNowView;
    @BindView(R.id.more_setting)
    View mMoreSettingView;
    @BindView(R.id.more_setting_icon)
    View mMoreSettingIcon;
    private boolean mIsEnglish = true;
    private float mCurrentInAppVolume = 1.0f;
    private float mCurrentBalanceValue = 0.5f;

    @OnClick(R.id.back)
    void onBack() {
        onBackPressed();
    }

    @OnCheckedChanged(R.id.hide_switch)
    void onChangedUseArtistImgAsBg(boolean value) {
        App.getInstance().getPreferencesUtility().setIsUsingArtistImageAsBackground(value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);

        ButterKnife.bind(SettingActivity.this);
        mAppVolumeSeekBar.setOnRangeChangedListener(this);
        mBalanceSeekBar.setOnRangeChangedListener(this);
        refreshData();
    }

    private void refreshInAppVolume() {
        mCurrentInAppVolume = App.getInstance().getPreferencesUtility().getInAppVolume();
        if (mCurrentInAppVolume >= 0) {
            mAppVolumeSeekBar.setValue(100 * mCurrentInAppVolume);
        } else {

        }
    }

    private void refreshBalanceValue() {
        mCurrentBalanceValue = App.getInstance().getPreferencesUtility().getBalanceValue();
        if (mCurrentBalanceValue < 0) mCurrentBalanceValue = 0;
        else if (mCurrentBalanceValue > 1) mCurrentBalanceValue = 1;
        mBalanceSeekBar.setValue(100 * mCurrentBalanceValue);
    }

    void refreshData() {
        refreshInAppVolume();
        refreshBalanceValue();
        mUseArtistImgAsBg.setChecked(App.getInstance().getPreferencesUtility().isUsingArtistImageAsBackground());

        Context context = SettingActivity.this;
        if (context != null) {
            String lang = LocaleHelper.getLanguage(context);
            mIsEnglish = lang.equals(EN);

            if (mIsEnglish) {
                mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_solid_left);
                mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_border_right);
                mSwitchToEn.setTextColor(getResources().getColor(R.color.flatOrange));
                mSwitchToVi.setTextColor(getResources().getColor(R.color.FlatWhite));
            } else {
                mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_border_left);
                mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_solid_right);
                mSwitchToEn.setTextColor(getResources().getColor(R.color.FlatWhite));
                mSwitchToVi.setTextColor(getResources().getColor(R.color.flatOrange));

            }
        }
    }

    @OnClick(R.id.switch_to_en)
    void switchToEN() {
        if (mIsEnglish) return;
        Activity activity = SettingActivity.this;
        if (activity != null) {
            LocaleHelper.setLocale(activity, "en");
            activity.recreate();
        }
    }

   /* @Override
    public void onSetStatusBarMargin(int value) {
        mStatusBar.getLayoutParams().height = value;
    }*/

    @OnClick(R.id.switch_to_vi)
    void switchToVI() {
        if (mIsEnglish) {
            Activity activity = SettingActivity.this;
            if (activity != null) {
                LocaleHelper.setLocale(activity, "vi");
                activity.recreate();
            }
        }

    }

   /* @Override
    public void onPaletteChanged() {
        super.onPaletteChanged();

        int color = Tool.getBaseColor();
        int alpha_color = Color.argb(0x22, Color.red(color), Color.green(color), Color.blue(color));
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                0xFF888888,
                color,
        };

        int[] trackColors = new int[]{
                0x22000000,
                alpha_color,
        };

        //  checkBox.setSupportButtonTintList(new ColorStateList(states, thumbColors));
//        DrawableCompat.setTintList(DrawableCompat.wrap(mUseArtistImgAsBg.getThumbDrawable()), new ColorStateList(states, thumbColors));
//        DrawableCompat.setTintList(DrawableCompat.wrap(mUseArtistImgAsBg.getTrackDrawable()), new ColorStateList(states, trackColors));


//        mAppVolumeSeekBar.setProgressColor(color);
        mAppVolumeSeekBar.requestLayout();

//        mBalanceSeekBar.setProgressColor(color);
        mBalanceSeekBar.requestLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         *//*   ColorStateList colorStateList = new ColorStateList(
                    new int[][] {new int[] {android.R.attr.state_pressed},new int[] {android.R.attr.state_focused}},
                    new int[] {color, alpha_color}
            );*//*

     *//* ((RippleDrawable) mSwitchToEn.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mSwitchToVi.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mCreateNowView.getBackground()).setColor(ColorStateList.valueOf(color));
            ((RippleDrawable) mMoreSettingView.getBackground()).setColor(ColorStateList.valueOf(color));*//*

        }

//        if (mIsEnglish) {
//            mSwitchToEn.setTextColor(color);
//        } else mSwitchToVi.setTextColor(color);

        if (mIsEnglish) {
            mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_solid_left);
            mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_border_right);
            mSwitchToEn.setTextColor(getResources().getColor(R.color.flatOrange));
            mSwitchToVi.setTextColor(getResources().getColor(R.color.FlatWhite));
        } else {
            mSwitchToEn.setBackgroundResource(R.drawable.ripple_16dp_border_left);
            mSwitchToVi.setBackgroundResource(R.drawable.ripple_16dp_solid_right);
            mSwitchToEn.setTextColor(getResources().getColor(R.color.FlatWhite));
            mSwitchToVi.setTextColor(getResources().getColor(R.color.flatOrange));
        }

    }*/

    public void setCurrentInAppVolume(float volume) {
        float vol = volume;
        if (vol < 0) vol = 0;
        else if (vol > 1) vol = 1;
        mCurrentInAppVolume = vol;
        App.getInstance().getPreferencesUtility().setInAppVolume(vol);
    }

    private void setCurrentBalanceValue(float value) {
        float vol = value;
        if (vol < 0) vol = 0;
        else if (vol > 1) vol = 1;
        mCurrentBalanceValue = vol;
        App.getInstance().getPreferencesUtility().setBalanceValue(vol);
    }

    @Override
    public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
        if (isFromUser) {
            switch (view.getId()) {
                case R.id.in_app_volume_seek_bar:
                    setCurrentInAppVolume(leftValue / 100);
                    break;
                case R.id.left_right_balance_seek_bar:
                    setCurrentBalanceValue(leftValue / 100);
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }

    @Override
    public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

    }
}