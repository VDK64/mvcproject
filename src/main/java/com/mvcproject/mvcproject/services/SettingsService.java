package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class SettingsService {
    @Autowired
    private UserRepo userRepo;
    @Value("${upload.path}")
    private String uploadPath;

    public void saveFile(MultipartFile file, User user) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath + "/" + user.getId());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + user.getId() + "/" + resultFilename));
            user.setAvatar(resultFilename);
            userRepo.save(user);
        }
    }
}
