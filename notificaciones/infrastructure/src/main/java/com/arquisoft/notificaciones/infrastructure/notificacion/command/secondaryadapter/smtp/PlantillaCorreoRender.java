package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlantillaCorreoRender {

    private static final String HUECO_TITULO = "{{titulo}}";
    private static final String HUECO_CUERPO = "{{cuerpo}}";
    private static final String HUECO_PIE = "{{pie}}";

    private static final List<String> HUECOS =
            List.of(HUECO_TITULO, HUECO_CUERPO, HUECO_PIE);

    private final FuentePlantillaCorreo fuente;

    @PostConstruct
    public void verificarHuecos() {
        String plantilla = fuente.obtener();
        for (String hueco : HUECOS) {
            if (!plantilla.contains(hueco)) {
                throw new PlantillaCorreoNoDisponibleException(hueco);
            }
        }
    }

    public String envolver(MensajeNotificacion mensaje) {
        return fuente.obtener()
                .replace(HUECO_TITULO, escapar(mensaje.asunto()))
                .replace(HUECO_CUERPO, escapar(mensaje.cuerpo()))
                .replace(HUECO_PIE, escapar(mensaje.pie()));
    }

    private static String escapar(String valor) {
        return UtilTexto.aplicarTrim(valor)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
