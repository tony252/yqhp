/*
 *  Copyright https://github.com/yqhp
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.yqhp.common.minio;

import com.yqhp.common.minio.config.MinioProperties;
import com.yqhp.common.minio.exception.MinioException;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * https://min.io/docs/minio/linux/developers/java/API.html
 *
 * @author jiangyitao
 */
@Slf4j
public class MinioTemplate {

    private final MinioClient minioClient;
    private final String publicEndpoint;
    private final String endpoint;
    private final String bucket;

    public MinioTemplate(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.publicEndpoint = minioProperties.getPublicEndpoint();
        this.endpoint = minioProperties.getEndpoint();
        this.bucket = minioProperties.getBucket();
    }

    public void createBucketIfAbsent(String bucket) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucket).build();
                log.info("make bucket: {}", bucket);
                minioClient.makeBucket(makeBucketArgs);
            } else {
                log.info("bucket: {}, already exists", bucket);
            }
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    public void uploadFile(File file, String fileKey) {
        try {
            UploadObjectArgs args = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .filename(file.getAbsolutePath())
                    .build();
            uploadObject(args);
        } catch (IOException e) {
            throw new MinioException(e);
        }
    }

    public void uploadMultipartFile(MultipartFile multipartFile, String fileKey) {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .contentType(multipartFile.getContentType())
                    .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                    .build();
            putObject(args);
        } catch (IOException e) {
            throw new MinioException(e);
        }
    }

    public void uploadObject(UploadObjectArgs args) {
        try {
            minioClient.uploadObject(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    public void putObject(PutObjectArgs args) {
        try {
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    public void deleteFile(String fileKey) {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(fileKey)
                .build();
        removeObject(args);
    }

    public void removeObject(RemoveObjectArgs args) {
        try {
            minioClient.removeObject(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    public boolean exists(String fileKey) {
        StatObjectArgs args = StatObjectArgs.builder()
                .bucket(bucket)
                .object(fileKey)
                .build();
        try {
            statObject(args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public StatObjectResponse statObject(StatObjectArgs args) {
        try {
            return minioClient.statObject(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    public void copyFile(String sourceFileKey, String targetFileKey) {
        CopySource source = CopySource.builder()
                .bucket(bucket)
                .object(sourceFileKey)
                .build();
        CopyObjectArgs args = CopyObjectArgs.builder()
                .source(source)
                .bucket(bucket)
                .object(targetFileKey)
                .build();
        copyObject(args);
    }

    public void copyObject(CopyObjectArgs args) {
        try {
            minioClient.copyObject(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }

    /**
     * 需要bucket Access Policy设置为public
     */
    public String getFileUrl(String fileKey) {
        return (StringUtils.hasText(publicEndpoint) ? publicEndpoint : endpoint)
                + "/" + bucket + "/" + fileKey;
    }

    public String getPresignedObjectUrl(GetPresignedObjectUrlArgs args) {
        try {
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            throw new MinioException(e);
        }
    }
}
