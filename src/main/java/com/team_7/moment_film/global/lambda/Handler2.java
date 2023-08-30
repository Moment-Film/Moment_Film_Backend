package com.team_7.moment_film.global.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Handler2 implements RequestHandler<S3Event, String> {
    private static final Logger logger = LoggerFactory.getLogger(Handler2.class);
    private final String REGEX = ".*\\.([^\\.]*)";
    private final String JPG_TYPE = "jpg";
    private final String JPG_MIME = "image/jpeg";
    private final String PNG_TYPE = "png";
    private final String PNG_MIME = "image/png";
    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotification.S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            String dstBucket = srcBucket + "-resized";


            // Infer the image type.
            Matcher matcher = Pattern.compile(REGEX).matcher(srcKey);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("올바른 사진이 아닙니다.");
            }
            String imageType = matcher.group(1);
            if (!(JPG_TYPE.equals(imageType)) && !(PNG_TYPE.equals(imageType))) {
                throw new IllegalArgumentException("올바른 확장자가 아닙니다.!");
            }

            // Download the image from S3 into a stream
            S3Client s3Client = S3Client.builder().build();
            InputStream s3Object = getObject(s3Client, srcBucket, srcKey);

            if (srcKey.startsWith("post/")) {
                String dstKey = "resized-" + srcKey;
                // Read the source image and resize it
                BufferedImage srcImage = ImageIO.read(s3Object);
                BufferedImage newImage = resizeImage(srcImage, 300, 446);

                // Re-encode image to target format
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(newImage, imageType, outputStream);

                // Upload new image to S3
                putObject(s3Client, outputStream, dstBucket, dstKey, imageType);

                logger.info("Successfully resized " + srcBucket + "/"
                        + srcKey + " and uploaded to " + dstBucket + "/" + dstKey);
            }

            if(srcKey.startsWith("profile/")){
                String dstKey = "resized-" + srcKey;
                // Read the source image and resize it
                BufferedImage srcImage = ImageIO.read(s3Object);
                BufferedImage newImage = resizeImage(srcImage, 50, 50);

                // Re-encode image to target format
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(newImage, imageType, outputStream);

                // Upload new image to S3
                putObject(s3Client, outputStream, dstBucket, dstKey, imageType);

                logger.info("Successfully resized " + srcBucket + "/"
                        + srcKey + " and uploaded to " + dstBucket + "/" + dstKey);
            }
            return "Ok";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getObject(S3Client s3Client, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    private void putObject(S3Client s3Client, ByteArrayOutputStream outputStream,
                           String bucket, String key, String imageType) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Integer.toString(outputStream.size()));
        if (JPG_TYPE.equals(imageType)) {
            metadata.put("Content-Type", JPG_MIME);
        } else if (PNG_TYPE.equals(imageType)) {
            metadata.put("Content-Type", PNG_MIME);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .build();


        // Uploading to S3 destination bucket
        logger.info("Writing to: " + bucket + "/" + key);
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(outputStream.toByteArray()));
        }
        catch(AwsServiceException e)
        {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Resizes (shrinks) an image into a small, thumbnail-sized image.
     *
     * The new image is scaled down proportionally based on the source
     * image. The scaling factor is determined based on the value of
     * MAX_DIMENSION. The resulting new image has max(height, width)
     * = MAX_DIMENSION.
     *
     * @param srcImage BufferedImage to resize.
     * @return New BufferedImage that is scaled down to thumbnail size.
     */
    private BufferedImage resizeImage(BufferedImage srcImage, float maxWidth, float maxHeight) {
        int srcHeight = srcImage.getHeight();
        int srcWidth = srcImage.getWidth();
        // Infer scaling factor to avoid stretching image unnaturally
        float scalingFactor = Math.min(
                maxWidth / srcWidth, maxHeight / srcHeight);
        int width = (int) (scalingFactor * srcWidth);
        int height = (int) (scalingFactor * srcHeight);

        BufferedImage resizedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        // Fill with white before applying semi-transparent (alpha) images
        graphics.setPaint(Color.white);
        graphics.fillRect(0, 0, width, height);
        // Simple bilinear resize
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }
}