package wangbin.graduation.com.camera2demo.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by momo on 2018/3/9.
 */

public class Image implements Parcelable {
    private String mPath;
    private String mName;
    private boolean isVideo;
    private String duration;

    public Image() {

    }

    protected Image(Parcel in) {
        mPath = in.readString();
        mName = in.readString();
        duration = in.readString();
        isVideo = Boolean.parseBoolean(in.readString());
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Image other = (Image) obj;
            return this.getPath().equalsIgnoreCase(other.getPath());
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mName);
        dest.writeString(duration);
        dest.writeString(String.valueOf(isVideo));
    }
}
