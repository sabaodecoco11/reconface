package br.ufg.reconface.config;

import br.ufg.reconface.service.AppPropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticServing implements WebMvcConfigurer {
    @Autowired
    AppPropertiesService appPropertiesService;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry){
        registry
                .addResourceHandler("/recognition/**")
                    .addResourceLocations("file:".concat(appPropertiesService.getStorageDir()));//diretório permitido
    }
}
