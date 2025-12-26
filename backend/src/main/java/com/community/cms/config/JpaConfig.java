package com.community.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정
 * @CreatedDate, @LastModifiedDate 등의 JPA Auditing 기능 활성화
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
