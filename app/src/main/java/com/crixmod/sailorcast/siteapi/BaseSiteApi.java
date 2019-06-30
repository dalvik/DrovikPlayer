package com.crixmod.sailorcast.siteapi;

import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCChannel;
import com.crixmod.sailorcast.model.SCChannelFilter;
import com.crixmod.sailorcast.model.SCVideo;

/**
 * Created by fire3 on 14-12-26.
 */
abstract public class BaseSiteApi {

    /** add support jsoup start **/
    public void doGetChannelAlbums(SCChannel channel, int pageNo, int pageSize, String cat, String area, OnGetAlbumsListener listener){

    }

    public void doGetEposideLis(SCChannel channel, int pageNo, int pageSize, String url, String alumbId, OnGetAlbumsListener.OnGetEpisodeListener listener){

    }

    /** add support jsoup end **/

    abstract public void doSearch(String key, OnGetAlbumsListener listener);
    /* pageNo start from 1 */

    abstract public void doGetAlbumVideos(SCAlbum album, int pageNo, int pageSize,  OnGetVideosListener listener);
    abstract public void doGetAlbumDesc(SCAlbum album, OnGetAlbumDescListener listener);

    abstract public void doGetVideoPlayUrl(SCVideo video, OnGetVideoPlayUrlListener listener);
    /* pageNo start from 1 */

    abstract public void doGetChannelAlbums(SCChannel channel, int pageNo, int pageSize, OnGetAlbumsListener listener);
    abstract public void doGetChannelFilter(SCChannel channel, OnGetChannelFilterListener listener);

    abstract public void doGetChannelAlbumsByFilter(SCChannel channel, int pageNo, int pageSize, SCChannelFilter filter, OnGetAlbumsListener listener);

}
