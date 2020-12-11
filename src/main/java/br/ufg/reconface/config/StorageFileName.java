package br.ufg.reconface.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class StorageFileName {
    private static final String defaultPhotoName = "photo";

    @Bean(name="defaultPhotoName")
    public String getDefaultPhotoName(){
        return defaultPhotoName;
    }
}
