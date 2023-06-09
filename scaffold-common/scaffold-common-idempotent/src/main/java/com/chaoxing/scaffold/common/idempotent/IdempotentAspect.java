package com.chaoxing.scaffold.common.idempotent;

import cn.hutool.core.lang.Assert;
import com.chaoxing.scaffold.common.idempotent.annotation.Idempotent;
import com.chaoxing.scaffold.common.idempotent.exception.IdempotentException;
import com.chaoxing.scaffold.common.idempotent.key.generator.IdempotentKeyGenerator;
import com.chaoxing.scaffold.common.idempotent.key.store.IdempotentKeyStore;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author hccake
 */
@Aspect
@RequiredArgsConstructor
public class IdempotentAspect {

	private final IdempotentKeyStore idempotentKeyStore;

	private final IdempotentKeyGenerator idempotentKeyGenerator;

	@Around("@annotation(idempotentAnnotation)")
	public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotentAnnotation) throws Throwable {
		// 获取幂等标识
		String idempotentKey = idempotentKeyGenerator.generate(joinPoint, idempotentAnnotation);

		// 校验当前请求是否重复请求
		boolean saveSuccess = idempotentKeyStore.saveIfAbsent(idempotentKey, idempotentAnnotation.duration(),
				idempotentAnnotation.timeUnit());
		Assert.isTrue(saveSuccess, () -> {
			throw new IdempotentException(idempotentAnnotation.message());
		});

		try {
			Object result = joinPoint.proceed();
			if (idempotentAnnotation.removeKeyWhenFinished()) {
				idempotentKeyStore.remove(idempotentKey);
			}
			return result;
		}
		catch (Throwable e) {
			// 异常时，根据配置决定是否删除幂等 key
			if (idempotentAnnotation.removeKeyWhenError()) {
				idempotentKeyStore.remove(idempotentKey);
			}
			throw e;
		}

	}

}
