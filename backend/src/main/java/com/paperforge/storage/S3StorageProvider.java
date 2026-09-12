package com.paperforge.storage;

import com.paperforge.security.FilenameSanitizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class S3StorageProvider implements StorageProvider {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageProvider(
            @Value("${paperforge.storage.s3.bucket:paperforge-storage}") String bucketName,
            @Value("${paperforge.storage.s3.region:us-east-1}") String region,
            @Value("${paperforge.storage.s3.endpoint:}") String endpoint,
            @Value("${paperforge.storage.s3.access-key:}") String accessKey,
            @Value("${paperforge.storage.s3.secret-key:}") String secretKey) {

        this.bucketName = bucketName;

        S3ClientBuilder builder = S3Client.builder().region(Region.of(region));

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        }

        this.s3Client = builder.build();
    }

    private String buildS3Key(StorageCategory category, String relativePath) {
        String sanitized = FilenameSanitizer.sanitizeFilename(relativePath);
        return category.getFolderName() + "/" + sanitized;
    }

    @Override
    public void saveFile(StorageCategory category, String relativePath, InputStream inputStream) throws IOException {
        String key = buildS3Key(category, relativePath);
        byte[] bytes = inputStream.readAllBytes();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentLength((long) bytes.length)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    @Override
    public InputStream getFile(StorageCategory category, String relativePath) throws IOException {
        String key = buildS3Key(category, relativePath);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        ResponseInputStream<GetObjectResponse> s3Stream = s3Client.getObject(getObjectRequest);
        return new ByteArrayInputStream(s3Stream.readAllBytes());
    }

    @Override
    public boolean deleteFile(StorageCategory category, String relativePath) throws IOException {
        String key = buildS3Key(category, relativePath);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public boolean exists(StorageCategory category, String relativePath) throws IOException {
        String key = buildS3Key(category, relativePath);
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public List<String> listFiles(StorageCategory category, String relativeDirectoryPath) throws IOException {
        String prefix = buildS3Key(category, relativeDirectoryPath);
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response res = s3Client.listObjectsV2(listReq);
        List<String> files = new ArrayList<>();
        for (S3Object obj : res.contents()) {
            files.add(obj.key());
        }
        return files;
    }

    @Override
    public long getStorageUsageBytes(StorageCategory category, String relativeDirectoryPath) throws IOException {
        String prefix = buildS3Key(category, relativeDirectoryPath);
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response res = s3Client.listObjectsV2(listReq);
        return res.contents().stream().mapToLong(S3Object::size).sum();
    }
}
