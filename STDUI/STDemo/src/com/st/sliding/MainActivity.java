package com.st.sliding;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.st.sliding.R;
import com.st.sliding.fragment.LeftMenuFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

public class MainActivity extends SlidingActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
     // set the Behind View
        setBehindContentView(R.layout.frame_menu);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        LeftMenuFragment menuFragment = new LeftMenuFragment();
        fragmentTransaction.replace(R.id.menu, menuFragment);
        fragmentTransaction.commit();
        initSlidingMenu();
    }
    
    private void initSlidingMenu() {
        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidth(50);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffset(80);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
