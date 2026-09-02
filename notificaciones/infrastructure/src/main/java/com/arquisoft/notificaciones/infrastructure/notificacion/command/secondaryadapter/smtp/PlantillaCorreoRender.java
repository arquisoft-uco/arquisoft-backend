package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlantillaCorreoRender {

    private final FuentePlantillaCorreo fuente;

    @PostConstruct
    public void verificarHuecos() {
        HuecosPlantillaCorreo.verificar(fuente.obtener());
    }

    // Una sola lectura de la fuente: si el monitor publica otra version a mitad de render, esta
    // llamada termina con la que empezo en vez de mezclar las dos.
    public String envolver(MensajeNotificacion mensaje) {
        return fuente.obtener()
                .replace(HuecosPlantillaCorreo.TITULO, escapar(mensaje.asunto()))
                .replace(HuecosPlantillaCorreo.CUERPO, escapar(mensaje.cuerpo()))
                .replace(HuecosPlantillaCorreo.PIE, escapar(mensaje.pie()));
    }

    private static String escapar(String valor) {
        return UtilTexto.aplicarTrim(valor)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
