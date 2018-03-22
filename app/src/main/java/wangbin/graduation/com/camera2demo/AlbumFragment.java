package wangbin.graduation.com.camera2demo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wangbin.graduation.com.camera2demo.Adapter.FolderAdapter;
import wangbin.graduation.com.camera2demo.Adapter.ImageAdapter;
import wangbin.graduation.com.camera2demo.Entity.Folder;
import wangbin.graduation.com.camera2demo.Entity.Image;
import wangbin.graduation.com.camera2demo.utils.TopPopup;

/**
 * Created by momo on 2018/3/8.
 */

public class AlbumFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private TopPopup mTopPopup;
    private Context mContext;
    private Activity mActivity;
    private int mScreenW;
    private int mScreenH;
    private Boolean isPop = false;

    private static final int LOAD_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    private boolean hasFolderGened = false;
    private List<Folder> mFolderList = new ArrayList<>();
    private List<Image> mImageList = new ArrayList<>();
    private List<Image> mAllImageList = new ArrayList<>();

    public FolderAdapter mFolderAdapter;
    public ImageAdapter mImageAdapter;
    private RecyclerView mPicRecycleView;
    private int mColumns = 3;
    private ImageView mArrow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mActivity = getActivity();
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().getLoaderManager().initLoader(0, null, mLoaderCallback);
        getScreenPixels();
        mArrow = (ImageView) view.findViewById(R.id.arrow_album);
        mPicRecycleView = (RecyclerView) view.findViewById(R.id.pic_recycle_view);
        mPicRecycleView.setLayoutManager(new GridLayoutManager(mContext, mColumns));
        mFolderAdapter = new FolderAdapter(mContext);
        mFolderAdapter.setOnItemClickListener(new FolderAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int pos, Folder folder) {
                mImageList.clear();
                mImageList.addAll(folder.getImageList());
                mImageAdapter.setData(mImageList, pos);
                mImageAdapter.notifyDataSetChanged();
                mTopPopup.dismiss();
            }
        });
        mTopPopup = new TopPopup(mContext, mScreenW, mScreenH, isPop, mFolderAdapter);
        mImageAdapter = new ImageAdapter(mContext, mScreenW, mColumns);
        mPicRecycleView.setAdapter(mImageAdapter);
        mView = (View) view.findViewById(R.id.picture_all);
        mView.setOnClickListener(this);
        mTopPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                doPopUpAnimation(-1f);
                mArrow.setImageResource(R.drawable.arrow_down);
                isPop = true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mImageAdapter.notifyDataSetChanged();
    }

    private void getScreenPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenW = metrics.widthPixels;
        mScreenH = metrics.heightPixels;

    }

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_all:
                getActivity().getLoaderManager().restartLoader(0, null, mLoaderCallback);
                mTopPopup.showAsDropDown(view, 0, 20);
                if (isPop) {
                    doPopUpAnimation(1f);
                    mArrow.setImageResource(R.drawable.arrow_up);
                    isPop = false;

                }
                break;
        }
    }

    private void doPopUpAnimation(float derection) {
        float x = derection * 180;
        Animation animation = new RotateAnimation(0, derection * 180,
                                                  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mArrow.startAnimation(animation);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new
            LoaderManager.LoaderCallbacks<Cursor>() {
                private final String[] IMAGE_PROJECTION = {
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID};

                @Override
                public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                    CursorLoader cursorLoader = new CursorLoader(getActivity(),
                                                                 MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                                                                 null, null, IMAGE_PROJECTION[2] + " DESC");
                    return cursorLoader;

                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

                    if (cursor != null) {
                        int i = 0;
                        int count = cursor.getCount();
                        if (count > 0) {
                            List<Image> tempImageList = new ArrayList<>();
                            cursor.moveToFirst();
                            do {
                                String path = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                                String name = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                                Image image = new Image();
                                image.setPath(path);
                                image.setName(name);
                                tempImageList.add(image);

                                if (!hasFolderGened) {
                                    File imageFile = new File(path);
                                    File floderFile = imageFile.getParentFile();
                                    Folder folder = new Folder();
                                    folder.setName(floderFile.getName());
                                    folder.setPath(floderFile.getPath());
                                    folder.setCover(image);
                                    if (!mFolderList.contains(folder)) {
                                        List<Image> imageList = new ArrayList<>();
                                        imageList.add(image);
                                        folder.setImageList(imageList);
                                        mFolderList.add(folder);
                                    } else {
                                        Folder f = mFolderList.get(mFolderList.indexOf(folder));
                                        f.getImageList().add(image);
                                    }
                                }
                            } while (cursor.moveToNext() && i++ < 1200);

                            mImageList = tempImageList;
                            //更新两个adapter

                            hasFolderGened = true;
                        }
                        if (mAllImageList.size() <= 0) {
                            mAllImageList.clear();
                            mAllImageList.addAll(mImageList);
                            mImageAdapter.setAllImageList(mAllImageList);
                        }

                        mFolderAdapter.setData(mFolderList);
                        mImageAdapter.setData(mImageList, -1);
                        mImageAdapter.setFolderList(mFolderList);
                    }

                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            };

}
