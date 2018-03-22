package wangbin.graduation.com.camera2demo.Entity;

import java.util.List;

/**
 * Created by momo on 2018/3/9.
 */

public class Folder {
    private String mName;
    private String mPath;
    private Image mCover;
    private List<Image> mImageList;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public Image getCover() {
        return mCover;
    }

    public void setCover(Image cover) {
        mCover = cover;
    }

    public List<Image> getImageList() {
        return mImageList;
    }

    public void setImageList(List<Image> imageList) {
        mImageList = imageList;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Folder other = (Folder) obj;
            return this.getPath().equalsIgnoreCase(other.getPath());
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(obj);
    }
}
