package org.shinigami.util;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.shinigami.config.Clip;
import org.shinigami.config.Video;

import java.io.File;

@ApplicationScoped
public class CropVideoUtil {

    public void cropVideo(FrameGrabber frameGrabber, String outputFile, long startTime, long endTime){
        try {
            frameGrabber.setTimestamp(startTime);

            int width = frameGrabber.getImageWidth();
            int height = frameGrabber.getImageHeight();
            int channels = frameGrabber.getAudioChannels();


            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(outputFile, width, height, channels);
            frameRecorder.setFormat("mp4");
            frameRecorder.setFrameRate(frameGrabber.getFrameRate());
            frameRecorder.setVideoBitrate(frameGrabber.getVideoBitrate());
//            frameRecorder.setVideoCodecName("libx264rgb");
//            frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            frameRecorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            frameRecorder.setAudioChannels(frameGrabber.getAudioChannels());
            frameRecorder.setSampleRate(frameGrabber.getSampleRate());
            frameRecorder.setAudioBitrate(frameGrabber.getAudioBitrate());
            frameRecorder.setAudioCodec(frameGrabber.getAudioCodec());
            frameRecorder.setVideoQuality(0); // MAX Quality!
            frameRecorder.start();

            Frame frame = null;
            while ((frame = frameGrabber.grabFrame()) != null && frameGrabber.getTimestamp() < endTime) {
                frameRecorder.record(frame);
            }
            frameRecorder.stop();
        } catch (Exception ex) {
            throw new IllegalArgumentException("cropVideo :: could not crop video: "+" / "+outputFile+" / "+startTime+" / "+endTime, ex);
        }
    }

    public void start(Video video) throws FFmpegFrameGrabber.Exception {
        Long delay = 0L;
        if(video.delay().isPresent()){
            delay = video.delay().get();
        }

        String videoPath = video.videoPath();
        String folderPath = videoPath.substring(0, videoPath.lastIndexOf("/")) + "/tmp/";
        File folder = new File(folderPath);
        if(!folder.exists()){
            folder.mkdir();
        }

        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(video.videoPath());
        frameGrabber.start();

        int count = 1;
        for(Clip clip: video.clips()){
            String filePath = folderPath + count++ + ".mp4";
            Long startTime = clip.start() + delay;
            Long endTime = clip.end() + delay;
            Log.info("Processing Video: " + clip.name());
            cropVideo(frameGrabber, filePath, startTime, endTime);
        }

        frameGrabber.stop();
    }
}
