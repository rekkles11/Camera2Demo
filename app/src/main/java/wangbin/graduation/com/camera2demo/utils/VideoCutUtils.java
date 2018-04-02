package wangbin.graduation.com.camera2demo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by chenxin on 2018/3/29.
 */

public class VideoCutUtils {
    private static final String TAG = "VideoCutUtils";

    /**
     * 获取视频帧
     * @param src 视频的路径
     * @param timeInterval 每帧的间隔时间(毫秒)
     * @return 图片集合
     */
    public static List<Bitmap> getFrames(String src, long timeInterval) {
        List<Bitmap> list = new ArrayList<>();
        MediaMetadataRetriever m = new MediaMetadataRetriever();
        m.setDataSource(src);
        long duration = Long.valueOf(m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.e(TAG, "视频时长：" + duration / 1000);
        int frames = duration > timeInterval ? (int) (duration / timeInterval) : 1;
        for (int i = 0; i < frames; i++) {
            Bitmap bitmap = m.getFrameAtTime(i * timeInterval);
            list.add(bitmap);
        }
        return list;
    }

    /**
     * 裁剪视频
     * @param context
     * @param inputFile 需要被裁剪的视频源文件
     * @param outputFile 裁剪后输出的文件目录
     * @param startMS 开始时间(毫秒)
     * @param endMs 结束时间(毫秒)
     */
    public static void trimVideo(final Context context, final String inputFile, String outputFile, int startMS, long endMs) {
        printLog("trimVideo");
        String time = new SimpleDateFormat("yyyymmdd_hhmmss", Locale.getDefault()).format(new Date());
        String cutFileName = time + "_videoCut.mp4";
        final String compressFileName = outputFile + time + "_videoCompress.mp4";
        outputFile = outputFile + cutFileName;
        String start = convertSecondsToTime(startMS / 1000);
        String duration = convertSecondsToTime((endMs - startMS) / 1000);
        String cmd = "-ss " + start + " -t " + duration + " -i " + inputFile + "  -vcodec copy -acodec copy " + outputFile;
        String[] command = cmd.split(" ");
        try {
            final String finalOutputFile = outputFile;
            FFmpeg.getInstance(context).execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess\n" + message);
                    Log.e(TAG, "path : " + finalOutputFile);
                    compressVideo(context, finalOutputFile, compressFileName);
                }

                @Override
                public void onProgress(String message) {
                    // Log.e(TAG, "onProgress" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure\n" + message);
                }

                @Override
                public void onStart() {
                    Log.e(TAG, "onStart");
                    Log.e(TAG, "inputFile: " + inputFile);
                    Log.e(TAG, "outputFile: " + finalOutputFile);
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish");

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }

    }

    public static void compressVideo(Context context, String inputFile, String outputFile) {
        printLog("compressVideo");
        String cmd = "-threads 2 -y -i " + inputFile + " -strict -2 -vcodec libx264 -preset ultrafast -crf 28 -acodec copy -ac 2 " + outputFile;
        String[] command = cmd.split(" ");
        try {
            FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "onSuccess\n" + message);
                }

                @Override
                public void onProgress(String message) {
                    // Log.e(TAG, "onProgress" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure\n" + message);
                }

                @Override
                public void onStart() {
                    Log.e(TAG, "onStart");
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public static String convertSecondsToTime(String path) {
        MediaMetadataRetriever m = new MediaMetadataRetriever();
        try {
            m.setDataSource(path);
            long duration = Long.valueOf(m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            return convertSecondsToTime(duration / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "00:00";
        } finally {
            m.release();
        }
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    private static void printLog(String string) {
        StringBuilder builder = new StringBuilder();
        int left = (60 - string.length()) / 2;
        int right = 60 - left - string.length();
        builder.append("|");
        for (int i = 1; i < left; i++) {
            builder.append(" ");
        }
        builder.append(string);
        for (int i = 1; i < right; i++) {
            builder.append(" ");
        }
        builder.append("|");
        Log.e(TAG, "------------------------------------------------------------");
        Log.e(TAG, "|                                                          |");
        Log.e(TAG, builder.toString());
        Log.e(TAG, "|                                                          |");
        Log.e(TAG, "------------------------------------------------------------");
    }
}
