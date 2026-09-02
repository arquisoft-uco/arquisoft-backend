package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.ConsumptionProbe;

public interface BucketResolver {

    // Consume, en vez de repartir buckets: getProxy es perezoso y el comando solo sale al consumir,
    // asi que quien reparte el bucket nunca llega a enterarse de que Redis no responde. Con el
    // consumo aqui dentro, el fallo se ve donde esta el estado que debe reaccionar a el.
    ConsumptionProbe consumir(String ip, boolean esLogin);

    boolean estaLimiteSolicitudesHabilitado();
}
