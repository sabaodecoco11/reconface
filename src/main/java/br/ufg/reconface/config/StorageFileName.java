package br.ufg.reconface.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StorageFileName {
    private static final String defaultPhotoName = "photo";
    private static final String defaultFaceName = "userface";
    private static final String defaultExtension = "jpg";

    @Bean(name="defaultFaceName")
    public String getDefaultFaceName(){
        return defaultFaceName;
    }
    @Bean(name="defaultPhotoName")
    public String getDefaultPhotoName(){
        return defaultPhotoName;
    }
    @Bean(name="defaultExtension")
    public String getDefaultExtension(){
        return defaultExtension;
    }

}
