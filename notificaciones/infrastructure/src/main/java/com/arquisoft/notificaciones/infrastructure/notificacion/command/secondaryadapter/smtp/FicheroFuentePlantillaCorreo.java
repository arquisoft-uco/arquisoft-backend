package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FicheroFuentePlantillaCorreo implements FuentePlantillaCorreo {

    private final String plantilla;

    public FicheroFuentePlantillaCorreo(
            ResourceLoader resourceLoader, NotificacionProperties properties) {
        this.plantilla = leer(resourceLoader, properties.getPlantilla());
    }

    @Override
    public String obtener() {
        return plantilla;
    }

    private static String leer(ResourceLoader resourceLoader, String ubicacion) {
        var recurso = resourceLoader.getResource(ubicacion);

        if (!recurso.exists()) {
            throw new PlantillaCorreoNoDisponibleException(ubicacion);
        }

        try (var entrada = recurso.getInputStream()) {
            String contenido = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);

            if (UtilTexto.esVacioONulo(contenido)) {
                throw new PlantillaCorreoNoDisponibleException(ubicacion);
            }

            return contenido;
        } catch (IOException e) {
            throw new PlantillaCorreoNoDisponibleException(ubicacion, e);
        }
    }
}
