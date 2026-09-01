package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.Bucket;

public interface BucketResolver {

    Bucket resolveBucket(String ip);

    Bucket resolveLoginBucket(String ip);

    Bucket bucketSinLimite();

    boolean estaLimiteSolicitudesHabilitado();
}
