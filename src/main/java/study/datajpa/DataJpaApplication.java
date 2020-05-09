package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing  // Spring Data jpa가 제공하는 Auditing 사용하기 위해 필요한 어노테이션
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository")  스프링 부트 사용시 생략가능 (해당패키지 및 하위패키지 인식)
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}


	// BaseEntity에 등록자와 수정자 가지고 오기 위해 아래의 코드 필요
	@Bean
	public AuditorAware<String> auditorProvide() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
