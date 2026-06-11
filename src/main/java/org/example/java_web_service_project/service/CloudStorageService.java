package org.example.java_web_service_project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.java_web_service_project.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudStorageService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_SIZE = 15 * 1024 * 1024L;

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "raw",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );

            String secureUrl = (String) result.get("secure_url");
            log.info("File uploaded to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new AppException("Không thể tải file lên hệ thống lưu trữ", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File không được để trống", HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new AppException("File vượt quá dung lượng tối đa 15MB", HttpStatus.BAD_REQUEST);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new AppException("Chỉ chấp nhận file PDF hoặc Word (.doc, .docx)", HttpStatus.BAD_REQUEST);
        }
    }
}
