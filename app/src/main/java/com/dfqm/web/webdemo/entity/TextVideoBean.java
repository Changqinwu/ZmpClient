package com.dfqm.web.webdemo.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class TextVideoBean {


    /**
     * LastEditTime : 2017-04-26 12:05:58
     * playlist : [{"_id":"1","type":"2","url":"http://999.zimeiping.com/wcq/video/f1.mp4"},{"_id":"2","type":"1","url":"http://999.zimeiping.com/wcq/images/zhsh.jpg"},{"_id":"3","type":"2","url":"http://999.zimeiping.com/wcq/video/f2.mp4"}]
     * video : [{"_id":"1","type":"2","url":"http://999.zimeiping.com/wcq/video/f1.mp4"},{"_id":"3","type":"2","url":"http://999.zimeiping.com/wcq/video/f2.mp4"},{"_id":"4","type":"2","url":"http://999.zimeiping.com/wcq/video/ly.mp4"}]
     */

    private String LastEditTime;
    private List<PlaylistBean> playlist;
    private List<VideoBean> video;

    public String getLastEditTime() {
        return LastEditTime;
    }

    public void setLastEditTime(String LastEditTime) {
        this.LastEditTime = LastEditTime;
    }

    public List<PlaylistBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<PlaylistBean> playlist) {
        this.playlist = playlist;
    }

    public List<VideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoBean> video) {
        this.video = video;
    }

    public static class PlaylistBean {
        /**
         * _id : 1
         * type : 2
         * url : http://999.zimeiping.com/wcq/video/f1.mp4
         */

        private String _id;
        private String type;
        private String url;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class VideoBean {
        /**
         * _id : 1
         * type : 2
         * url : http://999.zimeiping.com/wcq/video/f1.mp4
         */

        private String _id;
        private String type;
        private String url;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
