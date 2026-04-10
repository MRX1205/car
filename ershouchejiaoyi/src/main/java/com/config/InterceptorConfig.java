package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.interceptor.AuthorizationInterceptor;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport{
	
	@Bean
    public AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAuthorizationInterceptor()).addPathPatterns("/**").excludePathPatterns("/static/**");
        super.addInterceptors(registry);
	}
	
	/**
	 * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/resources/")
        .addResourceLocations("classpath:/static/")
        .addResourceLocations("classpath:/admin/")
        .addResourceLocations("classpath:/img/")
        .addResourceLocations("classpath:/front/")
        .addResourceLocations("classpath:/public/");
        
        try {
            java.io.File path = new java.io.File(org.springframework.util.ResourceUtils.getURL("classpath:static").getPath());
            if(!path.exists()) { path = new java.io.File(""); }
            java.io.File upload = new java.io.File(path.getAbsolutePath(),"upload");
            String uploadPath = upload.getAbsolutePath().replace("\\", "/");
            if (!uploadPath.startsWith("/")) {
                uploadPath = "/" + uploadPath;
            }
            registry.addResourceHandler("/upload/**").addResourceLocations("file:" + uploadPath + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }

		super.addResourceHandlers(registry);
    }
}
