package com.dogar.mytaskmanager.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dogar.mytaskmanager.R;
import com.dogar.mytaskmanager.fragment.AboutFragment;
import com.dogar.mytaskmanager.fragment.AppListFragment;
import com.dogar.mytaskmanager.fragment.SettingsFragment;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFragment(AppListFragment.newInstance(), true, false, AnimationDirection.FROM_BOTTOM_TO_TOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.selectAll:
                return true;
            case R.id.deselectAll:
                return true;
            case R.id.settings:
                changeFragment(SettingsFragment.newInstance(),true,true,AnimationDirection.FROM_BOTTOM_TO_TOP);
                return true;
            case R.id.about:
                changeFragment(AboutFragment.newInstance(),true,true,AnimationDirection.FROM_BOTTOM_TO_TOP);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
