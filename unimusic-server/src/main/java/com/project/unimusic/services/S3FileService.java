package com.project.unimusic.services;

import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;
import org.springframework.core.io.InputStreamResource;

@Service
public class S3FileService {

    private final S3Client s3Client;

    public S3FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void downloadFile(String bucketName, String key, String downloadPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    }

    public InputStreamResource streamFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        return new InputStreamResource(s3Object);
    }
}
