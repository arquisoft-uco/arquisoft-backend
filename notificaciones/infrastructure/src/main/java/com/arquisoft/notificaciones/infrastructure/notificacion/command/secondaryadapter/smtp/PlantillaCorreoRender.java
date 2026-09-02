package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;

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
    //
    // Una sola pasada sobre la plantilla: cada marcador se sustituye una vez y el texto insertado
    // no se vuelve a inspeccionar. Con replace() encadenado, un asunto o un cuerpo que contuviera
    // el literal "{{cuerpo}}" o "{{pie}}" —escapar() no toca las llaves— disparaba la sustitucion
    // del marcador siguiente sobre ese texto. quoteReplacement neutraliza los "$" y "\" que el
    // valor pueda traer, que appendReplacement interpretaria como referencias de grupo.
    public String envolver(MensajeNotificacion mensaje) {
        Map<String, String> valores = Map.of(
                HuecosPlantillaCorreo.TITULO, escapar(mensaje.asunto()),
                HuecosPlantillaCorreo.CUERPO, escapar(mensaje.cuerpo()),
                HuecosPlantillaCorreo.PIE, escapar(mensaje.pie()));

        Matcher marcador = HuecosPlantillaCorreo.MARCADORES.matcher(fuente.obtener());
        var salida = new StringBuilder();
        while (marcador.find()) {
            marcador.appendReplacement(salida, Matcher.quoteReplacement(valores.get(marcador.group())));
        }
        marcador.appendTail(salida);
        return salida.toString();
    }

    private static String escapar(String valor) {
        return UtilTexto.aplicarTrim(valor)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
