package com.dogar.mytaskmanager.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.dogar.mytaskmanager.App;
import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.model.AppInfo;
import com.dogar.mytaskmanager.utils.CommonUtils;
import com.dogar.mytaskmanager.utils.MemoryUtil;
import com.kogitune.activity_transition.fragment.ExitFragmentTransition;
import com.kogitune.activity_transition.fragment.FragmentTransition;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;

public class MoreAppInfoFragment extends BaseFragment implements ExitFragmentTransition.ExitListener {
    public static final String APP_INFO_OBJ = "app_info";
    public static final int ANIM_DURATION = 1000;

    @Bind(R.id.imgAppIconInfo)
    ImageView appIcon;
    @Bind(R.id.packageName)
    TextView  packageName;
    @Bind(R.id.version)
    TextView  version;
    @Bind(R.id.installTime)
    TextView  installTime;
    @Bind(R.id.updateTime)
    TextView  updateTime;
    @Bind(R.id.tvPid)
    TextView  pid;
    @Bind(R.id.tvMemory)
    TextView  memory;

    @Bind(R.id.detail_card_view)
    CardView detailCard;
    @Inject
    Context  context;

    private int screenHeight;

    public static MoreAppInfoFragment newInstance() {
        MoreAppInfoFragment moreAppInfoFragment = new MoreAppInfoFragment();
        return moreAppInfoFragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_more_app_info;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        App.getInstance().component().inject(this);
        final ExitFragmentTransition exitFragmentTransition = FragmentTransition.with(this).to(appIcon).start(savedInstanceState);
        exitFragmentTransition.setExitListener(this);
        exitFragmentTransition.startExitListening();
        initScreenHeight();
        fillViews();
        animateInInfoPanel();
    }

    @Override
    public void onFragmentExit() {
        animateOutInfoPanel();
    }

    private void fillViews() {
        AppInfo currentApp = Parcels.unwrap(getArguments().getParcelable(APP_INFO_OBJ));
        packageName.setText(currentApp.getPackageName());
        version.setText(currentApp.getVersion());
        installTime.setText(CommonUtils.getStringDate(currentApp.getFirstInstallTimestamp(), context));
        updateTime.setText(CommonUtils.getStringDate(currentApp.getLastUpdateTimestamp(), context));
        pid.setText(String.valueOf(currentApp.getPid()));
        memory.setText(MemoryUtil.formatMemSize(context, currentApp.getMemoryInKb()));
    }

    private void animateInInfoPanel() {
        ViewTreeObserver viewTreeObserver = detailCard.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        appIcon.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        appIcon.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    int viewHeight = detailCard.getHeight();
                    ObjectAnimator moveAnim = ObjectAnimator.ofFloat(detailCard, "Y", screenHeight - viewHeight / 2 - getResources().getDimension(R.dimen.app_more_icon_size));
                    moveAnim.setDuration(ANIM_DURATION);
                    moveAnim.setInterpolator(new ReverseInterpolator());
                    moveAnim.start();
                }
            });
        }
    }

    private void initScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
    }

    private void animateOutInfoPanel() {
        ObjectAnimator moveAnim = ObjectAnimator.ofFloat(detailCard, "Y", screenHeight);
        moveAnim.setDuration(ANIM_DURATION);
        moveAnim.setInterpolator(new LinearInterpolator());
        moveAnim.start();
    }

    private class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }
}
