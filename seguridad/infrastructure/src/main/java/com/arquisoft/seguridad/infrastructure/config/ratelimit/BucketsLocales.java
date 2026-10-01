package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Cuotas en memoria que sustituyen a las de Redis mientras Redis no responde.
 *
 * <p>El desalojo por antigüedad de acceso no es un detalle de implementación: sin tope, la caché
 * crece con una entrada por IP vista durante la caída, y una caída de Redis se convierte en un
 * vector de agotamiento de memoria — la denegación de servicio que el limitador existe para evitar,
 * provocada por el propio limitador. Con el tope, el peor caso es que un atacante que rote más de
 * {@code maxIps} orígenes acabe desalojando su propia entrada y recupere cuota; eso degrada la
 * precisión del límite, no la disponibilidad del proceso, y es el lado correcto del intercambio.
 *
 * <p>El acceso va bajo un {@link ReentrantLock} explícito, no bajo {@code synchronized} ni un
 * {@link java.util.Collections#synchronizedMap}: en Java 21 un virtual thread que espera la entrada
 * a un monitor contendido queda clavado a su carrier, y durante una caída de Redis este cerrojo se
 * toma en cada consumo de cuota. Con {@code ReentrantLock} el que espera se aparca sin clavar. El
 * {@link LinkedHashMap} en orden de acceso se mantiene: {@code removeEldestEntry} da el tope exacto
 * que un {@code ConcurrentHashMap} con purga periódica solo aproxima.
 */
public final class BucketsLocales {

    private final ReentrantLock cerrojo = new ReentrantLock();
    private final Map<String, Bucket> buckets;

    public BucketsLocales(int maxIps) {
        this.buckets = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Bucket> masAntigua) {
                return size() > maxIps;
            }
        };
    }

    Bucket obtener(String clave, Supplier<BucketConfiguration> configuracion) {
        cerrojo.lock();
        try {
            return buckets.computeIfAbsent(clave, ignorada -> construir(configuracion.get()));
        } finally {
            cerrojo.unlock();
        }
    }

    int tamanio() {
        cerrojo.lock();
        try {
            return buckets.size();
        } finally {
            cerrojo.unlock();
        }
    }

    void limpiar() {
        cerrojo.lock();
        try {
            buckets.clear();
        } finally {
            cerrojo.unlock();
        }
    }

    // La cuota local se construye desde la misma BucketConfiguration que la distribuida, para que
    // no puedan divergir: degradar cambia el alcance del límite —pasa a ser por instancia— pero no
    // su valor.
    private static Bucket construir(BucketConfiguration configuracion) {
        var constructor = Bucket.builder();
        for (var limite : configuracion.getBandwidths()) {
            constructor.addLimit(limite);
        }
        return constructor.build();
    }
}
