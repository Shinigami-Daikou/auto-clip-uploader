package org.shinigami.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.InputStreamContent;


import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.shinigami.config.Clip;
import org.shinigami.config.Youtube;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class YoutubeUploadUtil {
    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/youtube.upload");

    private static final String APPLICATION_NAME = "auto-clip-uploader";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = YoutubeUploadUtil.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                        .build();
        Credential credential =
                new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Call function to create Video object.
     *
     * @return a video object
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    private Video fillVideoDetails(String originalTitle, List<String> tags, String categoryId, String privacyStatus, String description) {
        // Define the Video object, which will be uploaded as the request body.
        Video video = new Video();

        // Add the snippet object property to the Video object.
        VideoSnippet snippet = new VideoSnippet();
        snippet.setCategoryId(categoryId);
        snippet.setDescription(description);
        snippet.setTags(tags);
        snippet.setTitle(originalTitle + description);
        video.setSnippet(snippet);

        // Add the status object property to the Video object.
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(privacyStatus);
        video.setStatus(status);

        return video;
    }

    public void start(Youtube youtube, String videoPath, List<Clip> clips) throws GeneralSecurityException, IOException {
        YouTube youtubeService = getService();

        String clipDir = videoPath.substring(0, videoPath.lastIndexOf("/")) + "/tmp/";

        String originalTitle = "";
        if(youtube.originalName().isPresent())
            originalTitle = youtube.originalName().get() + " | Scene: ";

        List<String> tags = new ArrayList<>();
        if (youtube.tags().isPresent() && !youtube.tags().isEmpty())
            tags = youtube.tags().get();

        String categoryId = "24";
        if (youtube.categoryId().isPresent())
            categoryId = youtube.categoryId().get();

        String privacyStatus = "public";
        if (youtube.videoStatus().isPresent())
            privacyStatus = youtube.videoStatus().get();

        int count = 1;
        for(Clip clip: clips) {
            Video video = fillVideoDetails(originalTitle, tags, categoryId, privacyStatus, clip.name());

            String filePath = clipDir + count++ + ".mp4";
            File mediaFile = new File(filePath);
            if(mediaFile.exists()){
                Log.info("Uploading Video: " + clip.name());
                InputStreamContent mediaContent =
                        new InputStreamContent("application/octet-stream",
                                new BufferedInputStream(new FileInputStream(mediaFile)));
                mediaContent.setLength(mediaFile.length());

                // Define and execute the API request
                YouTube.Videos.Insert request = youtubeService.videos()
                        .insert("snippet,status", video, mediaContent);
                Video response = request.execute();
            } else {
                Log.info("Video does not exist: " + filePath);
            }
        }
    }

}
