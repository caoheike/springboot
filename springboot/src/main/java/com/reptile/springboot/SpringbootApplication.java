package com.reptile.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@ComponentScan(basePackages={"com.reptile"})
@SpringBootApplication
@EnableScheduling
public class SpringbootApplication extends WebMvcConfigurerAdapter {
	 @Override
     public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
          configurer.favorPathExtension(false);
      }
	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}
}
