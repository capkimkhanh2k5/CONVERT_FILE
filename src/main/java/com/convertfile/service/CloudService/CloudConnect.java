package com.convertfile.service.CloudService;

import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.convertfile.service.PropertiesService;

public class CloudConnect {
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {

            String cloudName = PropertiesService.getCloudinaryCloudName();
            String apiKey = PropertiesService.getCloudinaryApiKey();
            String apiSecret = PropertiesService.getCloudinaryApiSecret();
            boolean secure = PropertiesService.getCloudinaryUrlSecure();
            
            Map config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", secure
            );
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}
