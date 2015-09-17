package com.dogar.mytaskmanager.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dogar.mytaskmanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)           Toolbar  toolbar;
    @Bind(R.id.ramUsageInfoValue) TextView ramUsageLabel;

    private FragmentManager mFm = getSupportFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        setupToolbar();
    }

    public void goToTop() {
        mFm.popBackStack(mFm.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void changeFragment(Fragment f, boolean addToBackStack, boolean animate, AnimationDirection direction) {
        if (f == null) {
            return;
        }
        FragmentTransaction ft = mFm.beginTransaction();

        // Animations
        if (animate) {
            switch (direction) {
                case FROM_RIGHT_TO_LEFT:
                    ft.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_left,
                            R.anim.in_from_right, R.anim.out_to_right);
                    break;
                case FROM_LEFT_TO_RIGHT:
                    ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_right,
                            R.anim.in_from_left, R.anim.out_to_left);
                    break;
                case FROM_BOTTOM_TO_TOP:
                    ft.setCustomAnimations(R.anim.in_from_down, R.anim.out_to_down,
                            R.anim.in_from_up, R.anim.out_to_up);
                    break;
                case FROM_TOP_TO_BOTTOM:
                    ft.setCustomAnimations(R.anim.in_from_up, R.anim.out_to_up,
                            R.anim.in_from_down, R.anim.out_to_down);
                    break;
            }
        }

        // BackStack
        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        // Adding fragment
        Fragment oldFragment = mFm.findFragmentById(R.id.fragmentContainer);
        if (oldFragment != null) {
            ft.remove(oldFragment);
        }
        ft.add(R.id.fragmentContainer, f);

        // Commit transaction
        ft.commit();
    }

    protected void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    protected enum AnimationDirection {
        FROM_LEFT_TO_RIGHT,
        FROM_RIGHT_TO_LEFT,
        FROM_TOP_TO_BOTTOM,
        FROM_BOTTOM_TO_TOP
    }

}