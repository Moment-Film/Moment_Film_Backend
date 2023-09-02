package com.team_7.moment_film.domain.post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.team_7.moment_film.domain.post.exception.UploadException;
import com.team_7.moment_film.global.dto.S3Prefix;
import com.team_7.moment_film.global.statuscode.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.team_7.moment_film.global.dto.S3Prefix.FRAME;
import static com.team_7.moment_film.global.dto.S3Prefix.PROFILE;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 이미지를 S3에 업로드하고 업로드된 이미지의 S3 URL을 반환합니다.
     *
     * @param multipartFile 업로드할 이미지 파일
     * @return 업로드된 이미지의 S3 URL
     * @throws IllegalArgumentException 업로드 실패 시 발생하는 예외
     */
    public String upload(MultipartFile multipartFile, S3Prefix S3Prefix) {
        if (multipartFile == null || multipartFile.isEmpty()) return null;

        try {
            List<String> allowedExtentions = Arrays.asList("jpg","png","jpeg");

            String originalFilename = multipartFile.getOriginalFilename();
            String extention = extractExtension(originalFilename);

            if (!allowedExtentions.contains(extention) && S3Prefix.equals(PROFILE)) {
                throw new IllegalArgumentException("png, jpg, jpeg파일만 업로드 가능합니다.");
            }

            byte[] fileBytes = multipartFile.getBytes();
            String fileName = S3Prefix.getDirectory() + generateFileName(multipartFile.getOriginalFilename());
            String contentType = multipartFile.getContentType();
            putS3(fileBytes, fileName, contentType);
            String imageUrl = generateUnsignedUrl(fileName);
            String resizedImageUrl = generateResizedImageUrl(fileName);
            log.info("이미지 업로드 완료: " + imageUrl);
            if (S3Prefix.equals(FRAME)) {
                return imageUrl;
            }
            return resizedImageUrl;
        } catch (IOException e) {
            throw new UploadException(ErrorCodeEnum.UPLOAD_FAIL, e);
        }
    }

    /**
     * 이미지를 S3에 업로드합니다.
     *
     * @param fileBytes   업로드할 이미지의 바이트 배열
     * @param fileName    업로드할 이미지의 파일 이름
     * @param contentType 업로드할 이미지의 컨텐츠 타입
     */
    private void putS3(byte[] fileBytes, String fileName, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        metadata.setContentType(contentType);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        log.info("파일 생성: " + fileName);
    }

    /**
     * S3에서 이미지를 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지의 URL
     * @throws IllegalArgumentException 이미지 삭제 실패 시 발생하는 예외
     */
    public void delete(String imageUrl) {
        if (StringUtils.hasText(imageUrl)) {
            String fileName = extractObjectKeyFromUrl(imageUrl);
            try {
                String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
                if (!amazonS3.doesObjectExist(bucket, decodedFileName)) {
                    throw new AmazonS3Exception(decodedFileName + " 은 존재하지 않습니다");
                }
                amazonS3.deleteObject(bucket, decodedFileName);
                log.info("파일 삭제: " + decodedFileName);
            } catch (IllegalArgumentException e) {
                throw new UploadException(ErrorCodeEnum.FILE_DECODE_FAIL, e);
            }
        }
    }

    /**
     * 이미지 URL에서 S3 객체 키를 추출합니다.
     *
     * @param imageUrl 이미지의 URL
     * @return 추출된 S3 객체 키
     * @throws IllegalArgumentException 잘못된 URL 형식일 경우 발생하는 예외
     */
    private String extractObjectKeyFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return url.getPath().substring(1); // leading slash 제거
        } catch (Exception e) {
            throw new UploadException(ErrorCodeEnum.URL_INVALID, e);
        }
    }

    /**
     * 업로드할 이미지 파일의 원본 파일 이름으로 고유한 파일 이름을 생성합니다.
     *
     * @param originalFilename 업로드할 이미지 파일의 원본 파일 이름
     * @return 생성된 고유한 파일 이름
     * @throws IllegalArgumentException 파일 이름이 유효하지 않을 경우 발생하는 예외
     */
    private String generateFileName(String originalFilename) {
        if (StringUtils.hasText(originalFilename)) {
            String extension = extractExtension(originalFilename);
            String uniqueId = UUID.randomUUID().toString();
            return uniqueId + "." + extension;
        }
        throw new UploadException(ErrorCodeEnum.FILE_INVALID);
    }

    /**
     * 파일 이름에서 확장자를 추출합니다.
     *
     * @param originalFilename 파일 이름
     * @return 추출된 확장자
     * @throws IllegalArgumentException 확장자를 추출할 수 없을 경우 발생하는 예외
     */
    private String extractExtension(String originalFilename) {
        if (StringUtils.hasText(originalFilename)) {
            int extensionIndex = originalFilename.lastIndexOf(".");
            if (extensionIndex != -1) {
                return originalFilename.substring(extensionIndex + 1);
            }
        }
        throw new UploadException(ErrorCodeEnum.EXTRACT_INVALID);
    }

    /**
     * S3 객체에 대한 유효기간이 없는 서명되지 않은 URL을 생성합니다.
     *
     * @param objectKey S3 객체 키
     * @return 유효기간이 없는 서명되지 않은 URL
     */
    private String generateUnsignedUrl(String objectKey) {
        String baseUrl = "https://" + bucket + ".s3.amazonaws.com/";
        return baseUrl + objectKey;
    }

    /**
     * 리사이징된 이미지 URL을 생성합니다.
     *
     * @param objectKey S3 객체 키
     * @return 리사이징 이미지 URL
     */
    private String generateResizedImageUrl(String objectKey) {
        String resizedUrl = "https://" + bucket + "-resized.s3.amazonaws.com/resized-";
        return resizedUrl + objectKey;
    }

    /**
     * 리사이징 이미지 URL로 원본 이미지 URL을 생성합니다.
     *
     * @param resizedImageUrl 리사이징 이미지 URL
     * @return 원본 이미지 URL
     */
    public String generateOriginalImageUrl(String resizedImageUrl, S3Prefix s3Prefix) {
        if (resizedImageUrl == null) {
            return null;
        }
        String[] parts = resizedImageUrl.split("/");
        String uri = parts[2];
        String objectKey = parts[4];

        String originalUri = uri.replace("-resized", "");

        return "https://" + originalUri + "/" + s3Prefix.getDirectory() + objectKey;
    }



}