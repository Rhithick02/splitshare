package com.expensemanagement.splitshare.integration;

import com.expensemanagement.splitshare.constants.ImageKitConstants;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class ImageKitIntegration {

    @Value("${imagekit.public.key}")
    private String publicKey;

    @Value("${imagekit.private.key}")
    private String privateKey;

    @Value("${imagekit.url.endpoint}")
    private String imageKitUrl;

    private ImageKit imageKit;
    private Base64.Encoder encoder;

    @PostConstruct
    public void init() {
        imageKit = ImageKit.getInstance();
        Configuration config = new Configuration(publicKey, privateKey, imageKitUrl);
        imageKit.setConfig(config);
        encoder = Base64.getEncoder();
    }

    public Result uploadImage(MultipartFile image, Long groupId) {
        Result response = null;
        try {
            byte[] imageInBytes = image.getBytes();
            String base64Image = encoder.encodeToString(imageInBytes);
            String filename = groupId.toString() + ImageKitConstants.JPEG_EXTENSION;
            FileCreateRequest fileCreateRequest = new FileCreateRequest(base64Image, filename);
            fileCreateRequest.setFolder(ImageKitConstants.IMAGE_FOLDER_PATH);
            fileCreateRequest.setUseUniqueFileName(false);
            response = imageKit.upload(fileCreateRequest);
        } catch (IOException ex) {
            log.error("Error while processing the image for groupId = {}", groupId);
            // throw Internal Server Error
        } catch (Exception ex) {
            log.error("Error while uploading the image for groupId = {}", groupId);
            // throw Service unavailable Exception
        }
        return response;
    }

    public String getImage(Long groupId) {
        List<Map<String, String>> transformation = new ArrayList<>();
        Map<String, String> scale = new HashMap<>();
        scale.put(ImageKitConstants.SCALE_HEIGHT_KEY, ImageKitConstants.SCALE_HEIGHT_VALUE);
        scale.put(ImageKitConstants.SCALE_WIDTH_KEY, ImageKitConstants.SCALE_WIDTH_VALUE);
        transformation.add(scale);
        Map<String, Object> options = new HashMap<>();
        options.put("path", String.format("/%s/%s.%s", ImageKitConstants.IMAGE_FOLDER_PATH, groupId.toString(), ImageKitConstants.JPEG_EXTENSION));
        options.put("transformation", transformation);
        return imageKit.getUrl(options);
    }
}
