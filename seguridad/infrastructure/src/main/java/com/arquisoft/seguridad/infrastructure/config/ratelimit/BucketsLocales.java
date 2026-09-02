package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
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
 * <p>Es un {@link LinkedHashMap} en orden de acceso bajo un único cerrojo, y no un mapa concurrente:
 * la contención solo existe mientras dura la caída, y {@code removeEldestEntry} da el tope exacto
 * que un {@code ConcurrentHashMap} con purga periódica solo aproxima.
 */
public final class BucketsLocales {

    private final Map<String, Bucket> buckets;

    public BucketsLocales(int maxIps) {
        this.buckets = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Bucket> masAntigua) {
                return size() > maxIps;
            }
        });
    }

    Bucket obtener(String clave, Supplier<BucketConfiguration> configuracion) {
        return buckets.computeIfAbsent(clave, ignorada -> construir(configuracion.get()));
    }

    int tamanio() {
        return buckets.size();
    }

    void limpiar() {
        buckets.clear();
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
