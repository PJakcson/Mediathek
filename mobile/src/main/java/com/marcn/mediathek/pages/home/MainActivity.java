package com.marcn.mediathek.pages.home;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.StationOld;
import com.marcn.mediathek.model.asset.Asset;
import com.marcn.mediathek.pages.BaseActivity;
import com.marcn.mediathek.ui_fragments.LiveStreamsFragment;
import com.marcn.mediathek.ui_fragments.SendungenAbisZFragment;
import com.marcn.mediathek.ui_fragments.ZdfMissedVideoFragment;
import com.marcn.mediathek.utils.NetworkTasks;

import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String INTENT_LIVE_DRAWER_ITEM = "player-drawer-item";
    private LiveStreamsFragment liveStreamsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        FragmentManager fm = getSupportFragmentManager();
        liveStreamsFragment = (LiveStreamsFragment) fm.findFragmentByTag(LiveStreamsFragment.FRAGMENT_TAG);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null && intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1) >= 0) {
            int navId = intent.getIntExtra(INTENT_LIVE_DRAWER_ITEM, -1);
            navigationIdReceived(navId);
        } else {
//            navigationIdReceived(R.id.nav_zdf);
            if (liveStreamsFragment == null)
                liveStreamsFragment = LiveStreamsFragment.newInstance(1);
            loadCleanFragment(liveStreamsFragment, R.id.content_main, FRAGMENT_NAME_FIRST_PAGE, LiveStreamsFragment.FRAGMENT_TAG);
        }

        new Thread(() -> {
            List<Asset> episodes = NetworkTasks.listEpisodes();
        }).start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void navigationIdReceived(int id) {
        if (id == R.id.nav_live) {
            loadCleanFragment(new LiveStreamsFragment(), R.id.content_main, FRAGMENT_NAME_FIRST_PAGE, LiveStreamsFragment.FRAGMENT_TAG);
        } else if (id == R.id.nav_gallery) {
            loadCleanFragment(new ZdfMissedVideoFragment());
//        } else if (id == R.id.nav_zdf_mediathek) {
//            loadCleanFragment(new SendungenAbisZFragment());
//        } else if (id == R.id.nav_arte_mediathek) {
//        } else if (id == R.id.nav_ard_mediathek) {
        } else if (id == R.id.nav_all_series) {
            loadCleanFragment(new SendungenAbisZFragment());
        }

        StationOld station = null;
        switch (id) {
            case R.id.nav_zdf: station = new StationOld(getString(R.string.zdf_name)); break;
            case R.id.nav_ard: station = new StationOld(getString(R.string.ard_name)); break;
            case R.id.nav_swr: station = new StationOld(getString(R.string.swr_name)); break;
            case R.id.nav_zdf_neo: station = new StationOld(getString(R.string.zdf_neo_name)); break;
            case R.id.nav_arte: station = new StationOld(getString(R.string.arte_name)); break;
            case R.id.nav_zdf_info: station = new StationOld(getString(R.string.zdf_info_name)); break;
        }
        if (station != null)
            startChannelActivity(station);
    }

    private void loadCleanFragment(Fragment fragment) {
        loadCleanFragment(fragment, R.id.content_main);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }
}