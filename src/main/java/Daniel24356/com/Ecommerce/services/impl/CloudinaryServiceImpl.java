package Daniel24356.com.Ecommerce.services.impl;

import Daniel24356.com.Ecommerce.services.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadProfileImage(MultipartFile file) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary
                    .uploader()
                    .upload(file.getBytes(), ObjectUtils.asMap(
                            "folder", "secure_task_user_profiles",
                            "resource_type", "image",
                            "transformation", new com.cloudinary.Transformation().width(300).height(300).crop("fill")
                    ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
