package HireCraft.com.SpringBoot.services;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadProfileImage(MultipartFile file);

    public String uploadFile(MultipartFile file, String folderName);
}
