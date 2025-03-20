package org.shinigami;

import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.shinigami.config.AppConfig;
import org.shinigami.config.Video;
import org.shinigami.config.Youtube;
import org.shinigami.util.CropVideoUtil;
import org.shinigami.util.YoutubeUploadUtil;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@ApplicationScoped
public class ClipApplication implements QuarkusApplication {

    AppConfig config;
    CropVideoUtil cropVideoUtil;
    YoutubeUploadUtil youtubeUploadUtil;

    @Inject
    public void setConfig(AppConfig config) {
        this.config = config;
    }

    @Inject
    public void setCropVideoUtil(CropVideoUtil cropVideoUtil){
        this.cropVideoUtil = cropVideoUtil;
    }

    @Inject
    public void setYoutubeUploadUtil(YoutubeUploadUtil youtubeUploadUtil){
        this.youtubeUploadUtil = youtubeUploadUtil;
    }

    @Override
    public int run(String... args) throws ConfigurationException, IOException, GeneralSecurityException, InterruptedException {
        Video video = config.video();
        if (video.videoPath() == null || video.videoPath().isEmpty() || video.clips() == null || video.clips().isEmpty()){
            throw new ConfigurationException("Mandatory config not provided. Exiting......");
        }

        Log.info("Video processing Starts: ");
        cropVideoUtil.start(video);
        Log.info("Video processing Ends.");

        Youtube youtube = config.youtube();
        if(youtube != null){
            Log.info("Youtube Upload Starts: ");
            youtubeUploadUtil.start(youtube, video.videoPath(), video.clips());
            Log.info("Youtube Upload Ends.");
        }
        return 0;
    }
}
