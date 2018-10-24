package com.drovik.player.video.ui.pager;

import com.android.library.ui.pager.BasePager;
import com.crixmod.sailorcast.model.SCChannel;
import com.drovik.player.R;

public abstract class BaseMoviePager extends BasePager {

    public static int[] mTabTitle = {R.string.channel_movie, R.string.channel_show, R.string.channel_comic, R.string.channel_documentary,R.string.channel_variety };
    public int[] mChannel = {SCChannel.MOVIE, SCChannel.SHOW, SCChannel.COMIC, SCChannel.DOCUMENTARY, SCChannel.VARIETY};
    public static final String EXTRA_CHANNEL_ID = "channelID";
    public final String ARG_SITE_ID = "siteID";
}
