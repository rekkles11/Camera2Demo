package wangbin.graduation.com.camera2demo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxin on 18/3/15.
 */

public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    private static final int GRANTED = PackageManager.PERMISSION_GRANTED;

    /**
     * 检查权限
     * @param context
     * @param permissions
     * @return 返回没有开启的权限
     */
    public static List<String> checkPermissions(Context context, String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permissionItem : permissions) {
            if (ContextCompat.checkSelfPermission(context, permissionItem) != GRANTED) {
                list.add(permissionItem);
            }
        }
        return list;
    }

    /**
     * 检查并且打开权限
     * @param context
     * @param permissions
     */
    public static boolean checkAndOpenPermissions(Context context, String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permissionItem : permissions) {
            if (ContextCompat.checkSelfPermission(context, permissionItem) != GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionItem)) {
                    Log.e(TAG, "为了App的正常运行，请开启[" + permissionItem + "]权限");
                }
                list.add(permissionItem);
            }
        }

        if (list.size() > 0) {
            ActivityCompat.requestPermissions((Activity) context, permissions, REQUEST_CODE);
            return false;
        } else {
            Log.e(TAG, "权限已经全部开启!");
            return true;
        }
    }

    /**
     * 打开权限后的回调，需要在activity里面的onRequestPermissionsResult()方法中调用
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static List<String> onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> noGrantedList = new ArrayList<>();
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != GRANTED) {
                    noGrantedList.add(permissions[i]);
                    Log.e(TAG, "权限[" + permissions[i] + "]已经拒绝授权");
                } else {
                    Log.e(TAG, "权限[" + permissions[i] + "]已经授权");
                }
            }
        }

        return noGrantedList;
    }

    /**
     * 去设置界面打开权限
     * @param context
     */
    public static void gotoSystemSetting(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("权限申请:").setMessage("请前往设置打开权限!").setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }).setNegativeButton("下次吧", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }
}
