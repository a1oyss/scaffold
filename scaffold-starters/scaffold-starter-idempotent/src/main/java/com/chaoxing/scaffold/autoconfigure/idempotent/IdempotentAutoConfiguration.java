package com.chaoxing.scaffold.autoconfigure.idempotent;

import com.chaoxing.common.idempotent.IdempotentAspect;
import com.chaoxing.common.idempotent.key.generator.IdempotentKeyGenerator;
import com.chaoxing.common.idempotent.key.generator.impl.DefaultIdempotentKeyGenerator;
import com.chaoxing.common.idempotent.key.store.IdempotentKeyStore;
import com.chaoxing.common.idempotent.key.store.impl.InMemoryIdempotentKeyStore;
import com.chaoxing.common.idempotent.key.store.impl.RedisIdempotentKeyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 幂等自动装配
 *
 * @author lishangbu
 * @date 2022/10/18
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

	/**
	 * 默认的幂等前缀生成器
	 * @return 幂等Key生成器
	 */
	@Bean
	@ConditionalOnMissingBean(IdempotentKeyGenerator.class)
	public IdempotentKeyGenerator idempotentKeyGenerator() {
		return new DefaultIdempotentKeyGenerator();
	}

	/**
	 * 默认的幂等键存储器
	 * @param properties 幂等属性配置
	 * @return 幂等Key存储
	 */
	@Bean
	@ConditionalOnMissingBean(IdempotentKeyStore.class)
	public IdempotentKeyStore idempotentKeyStore(IdempotentProperties properties) {
		IdempotentProperties.KeyStoreType keyStoreType = properties.getKeyStoreType();
		if (keyStoreType.equals(IdempotentProperties.KeyStoreType.REDIS)) {
			return new RedisIdempotentKeyStore();
		}
		else {
			return new InMemoryIdempotentKeyStore();
		}
	}

	/**
	 * 幂等切面
	 * @param idempotentKeyStore 幂等key仓库
	 * @return IdempotentAspect
	 */
	@Bean
	public IdempotentAspect idempotentAspect(IdempotentKeyStore idempotentKeyStore,
											 IdempotentKeyGenerator idempotentKeyGenerator) {
		return new IdempotentAspect(idempotentKeyStore, idempotentKeyGenerator);
	}

}
