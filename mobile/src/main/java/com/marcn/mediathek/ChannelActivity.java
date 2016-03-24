package com.marcn.mediathek;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.Video;
import com.marcn.mediathek.stations.Station;
import com.marcn.mediathek.ui_fragments.SeriesWidgetFragment;
import com.marcn.mediathek.ui_fragments.VideoWidgetFragment;
import com.marcn.mediathek.utils.Anims;
import com.marcn.mediathek.utils.Transitions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ChannelActivity extends BaseActivity
        implements AppBarLayout.OnOffsetChangedListener {

    public static final String INTENT_STATION_TITLE = "station-title";
    public static final int INT_WIDGET_VIDEO = 0;
    public static final int INT_WIDGET_SERIES = 1;
//    public static final String INTENT_SENDER_JSON = "station-json";

//    private StationOld mStation;
    private Station mStation;
    private Video mVideo;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private boolean mToolbarIsShown = false;
    private int mToolbarScrollRange = -1;
    private Context mContext;
    private ImageView mThumbnail;
    private FloatingActionButton mFab;
    private Episode mCurrentEpisode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = toolbar.getContext();
        mThumbnail = (ImageView) findViewById(R.id.imageThumbnail);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        Anims.fadeOut(mFab, 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            String title = intent.getStringExtra(INTENT_STATION_TITLE);
            mStation = Station.createStation(title);

            if(getSupportActionBar() != null)
                getSupportActionBar().setTitle(title);
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
//        appBarLayout.addOnOffsetChangedListener(this);

//        getIntentThumbnail();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadWidgets();
                downloadLiveStreamData();
            }
        }, 500);
    }

    private void downloadLiveStreamData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCurrentEpisode = mStation.getCurrentEpisode();
//                mVideo = XmlParser.getLivestreamFromChannel(mContext, mStation);
                if (mCurrentEpisode == null) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupHeaderView();
                    }
                });
            }
        }).start();
    }

    private void loadWidgets() {
        if (mStation == null || mStation.getTopLevelCategories() == null) return;

        if (mStation.getTopLevelCategories() != null) {
            for (String key : mStation.getTopLevelCategories().keySet()) {
                loadWidget(key, INT_WIDGET_VIDEO);
            }
        }

        if (mStation.getSeriesWidgets() != null) {
            for (String key : mStation.getSeriesWidgets().keySet()) {
                loadWidget(key, INT_WIDGET_SERIES);
            }
        }
    }

    private void loadWidget(String widgetKey, int type) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment frag;
        if (type == INT_WIDGET_VIDEO)
            frag = VideoWidgetFragment.newInstance(mStation.getTitle(), null, widgetKey);
        else
            frag = SeriesWidgetFragment.newInstance(mStation.getTitle(), null, widgetKey);
        transaction.add(R.id.widgetContainer, frag, widgetKey);
        transaction.commit();
    }

    private void setupHeaderView() {
        if (findViewById(R.id.imageChannel) != null)
            ((TextView) findViewById(R.id.imageChannel)).setText(mStation.toString());
//            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.station.getLogoResId());
        if (findViewById(R.id.textTitle) != null)
            ((TextView) findViewById(R.id.textTitle)).setText(mCurrentEpisode.getTitle());

        if (findViewById(R.id.textDetail) != null)
            ((TextView) findViewById(R.id.textDetail)).setText(mCurrentEpisode.getDescription());

        if (mFab != null) {
            Anims.fadeIn(mFab);
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptions activityOptions = prepareInternalPlayerTransition();
                    playVideoWithInternalPlayer(mStation.getLiveStream().getStreamUrl(), activityOptions);
                }
            });
            mFab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    playVideoExternal(mStation.getLiveStream().getStreamUrl(), mStation.toString(), Episode.ACTION_SHARE_VIDEO_DIALOG);
                    return true;
                }
            });
        }

        if (mThumbnail != null && mCurrentEpisode.getThumbUrl() != null)
            Picasso.with(mContext)
                    .load(mCurrentEpisode.getThumbUrl())
//                    .config(Bitmap.Config.RGB_565)
//                    .resize(Constants.SIZE_THUMB_BIG_X, Constants.SIZE_THUMB_BIG_Y)
//                    .onlyScaleDown()
//                    .centerCrop()
                    .into(loadTarget);
    }

    private Target loadTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (mThumbnail != null)
                mThumbnail.setImageBitmap(bitmap);
            if (bitmap != null)
                themeActivity(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };

    private void themeActivity(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch swatch = p.getDarkVibrantSwatch() != null ?
                p.getDarkVibrantSwatch() : p.getDarkMutedSwatch();
        if (swatch != null) {
            float[] hsl = swatch.getHsl();
            hsl[2] *= 0.98f;

            mCollapsingToolbarLayout.setStatusBarScrimColor(Color.HSVToColor(hsl));
            mCollapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
            mCollapsingToolbarLayout.setBackgroundColor(swatch.getRgb());
        }
    }

    @SuppressWarnings("unchecked")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ActivityOptions prepareInternalPlayerTransition() {
        mThumbnail.setTransitionName("thumb");
        Transitions.saveBitmapFromImageView(this, mThumbnail, Transitions.PLAYER_THUMBNAIL);
        return ActivityOptions.makeSceneTransitionAnimation(this,
                new Pair<View, String>(mThumbnail, ""),
                new Pair<View, String>(mFab, ""));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    void navigationIdReceived(int id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_LIVE_DRAWER_ITEM, id);
        startActivity(intent);
    }

//    private void loadWidget(int Type, int resId) {
////        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////        transaction.replace(resId, VideoWidgetFragment.newInstance(mSendung, Type));
////        transaction.commit();
//    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mToolbarScrollRange == -1) {
            mToolbarScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (mToolbarScrollRange + verticalOffset == 0) {
            if (mVideo != null)
                mCollapsingToolbarLayout.setTitle(mVideo.title);
            mToolbarIsShown = true;
        } else if (mToolbarIsShown) {
            mCollapsingToolbarLayout.setTitle("");
            mToolbarIsShown = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    void setExitTransition() {
        getWindow().setExitTransition(new Explode());
    }

//    private void prepareBitmap() {
//        Bitmap bmp = ((BitmapDrawable) mThumbnail.getDrawable()).getBitmap();
//        Storage.saveBitmapOnDisk(this, bmp);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private ActivityOptions createOptions() {
//        mThumbnail.setTransitionName("thumb");
//        Pair<View, String> pair1 = Pair.create((View) mThumbnail, "");
//        Pair<View, String> pair2 = Pair.create((View) mFab, "");
//        Pair[] pairs = new Pair[2];
//        pairs[0] = pair1;
//        pairs[1] = pair2;
//
//        return ActivityOptions.makeSceneTransitionAnimation(this, pairs);
//    }
}

//    private void setupHeaderView(final Video liveStream) {
//        if (liveStream == null) return;
//        if (findViewById(R.id.imageChannel) != null)
//            ((TextView) findViewById(R.id.imageChannel)).setText(mStation.title);
////            ((ImageView) findViewById(R.id.imageChannel)).setImageResource(sendung.station.getLogoResId());
//        if (findViewById(R.id.textTitle) != null)
//            ((TextView) findViewById(R.id.textTitle)).setText(liveStream.getTitle());
//
//        if (findViewById(R.id.textDetail) != null)
//            ((TextView) findViewById(R.id.textDetail)).setText(liveStream.detail);
//
//        if (mFab != null) {
//            Anims.fadeIn(mFab);
//            mFab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ActivityOptions activityOptions = prepareInternalPlayerTransition();
//                    playVideoWithInternalPlayer(liveStream.getLiveM3U8(), activityOptions);
//                }
//            });
//            mFab.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    playVideoExternal(liveStream.getLiveM3U8(), mStation.title, Episode.ACTION_SHARE_VIDEO_DIALOG);
//                    return true;
//                }
//            });
//        }
//
//        if (mThumbnail != null && liveStream.thumb_url != null)
//            Picasso.with(mContext)
//                    .load(liveStream.thumb_url)
//                    .config(Bitmap.Config.RGB_565)
//                    .into(mThumbnail);
//    }
