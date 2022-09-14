package com.example.littleProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //啟動快取
public class LittleProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LittleProjectApplication.class, args);
	}

}
