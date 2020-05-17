package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    // 인터페이스 기반의 close projection
    //String getUsername();

    // 인터페이스 기반의 open projection
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
