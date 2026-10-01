package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

// Solo con proveedor=smtp: es el unico que maqueta HTML. Sin esta condicion, un entorno en modo
// log exigiria la plantilla en Redis para arrancar y no la usaria para nada.
@Component
@ConditionalOnProperty(name = "notificacion.proveedor", havingValue = "smtp")
public class RedisFuentePlantillaCorreo implements FuentePlantillaCorreo {

    private final StringRedisTemplate redis;
    private final String clave;
    private final AtomicReference<String> plantilla = new AtomicReference<>();

    public RedisFuentePlantillaCorreo(
            StringRedisTemplate redis, NotificacionProperties properties) {
        this.redis = redis;
        this.clave = properties.getPlantilla();
        this.plantilla.set(leer());
    }

    @Override
    public String obtener() {
        return plantilla.get();
    }

    // Arranque y refresco pasan por el mismo leer(), asi que la version publicada siempre fue
    // verificada. Quien lo llama decide que hacer con el fallo: el constructor aborta el contexto,
    // MonitorPlantillaCorreo se queda con la anterior.
    public boolean recargar() {
        String candidata = leer();
        return !candidata.equals(plantilla.getAndSet(candidata));
    }

    private String leer() {
        String contenido;

        try {
            contenido = redis.opsForValue().get(clave);
        } catch (RuntimeException e) {
            throw new PlantillaCorreoNoDisponibleException(clave, e);
        }

        if (UtilTexto.esVacioONulo(contenido)) {
            throw new PlantillaCorreoNoDisponibleException(clave);
        }

        HuecosPlantillaCorreo.verificar(contenido);
        return contenido;
    }
}
