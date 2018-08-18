package com.dev.kit.basemodule.util;

import android.os.Handler;
import android.os.Looper;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cuiyan on 2018/8/9.
 */
public class VideoUtil {
    public static void mergeVideos(final String targetVideoSavingPath, final List<String> videosPathList, final VideoMergeListener mergeListener) {
        final Handler handler = new Handler(Looper.getMainLooper());
        Thread mergeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mergeListener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mergeListener.onMergeStart();
                        }
                    });
                }
                try {
                    Movie[] inMovies = new Movie[videosPathList.size()];
                    int index = 0;
                    for (String videoPath : videosPathList) {
                        inMovies[index] = MovieCreator.build(videoPath);
                        index++;
                    }
                    // 分别取出音轨和视频
                    List<Track> videoTracks = new LinkedList<>();
                    List<Track> audioTracks = new LinkedList<>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }

                    // 合并到最终的视频文件
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }
                    Container out = new DefaultMp4Builder().build(result);
                    FileChannel fc = new RandomAccessFile(targetVideoSavingPath, "rw").getChannel();
                    out.writeContainer(fc);
                    fc.close();
                    if (mergeListener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mergeListener.onMergeSuccess(targetVideoSavingPath);
                            }
                        });
                    }
                } catch (final Exception e) {
                    if (mergeListener != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mergeListener.onMergeFailed(e);
                            }
                        });
                    }
                }
            }
        });
        mergeThread.start();
    }

    public interface VideoMergeListener {
        void onMergeStart();

        void onMergeSuccess(String mergedVideoPath);

        void onMergeFailed(Exception e);
    }
}
