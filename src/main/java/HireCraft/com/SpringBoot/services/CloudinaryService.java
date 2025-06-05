package HireCraft.com.SpringBoot.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    /**
     * Uploads the given file to Cloudinary and returns the secure URL.
     */
    String uploadProfileImage(MultipartFile file);
}
