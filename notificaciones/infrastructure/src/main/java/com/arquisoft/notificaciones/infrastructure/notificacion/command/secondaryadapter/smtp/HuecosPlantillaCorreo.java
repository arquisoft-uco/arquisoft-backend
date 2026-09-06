package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class HuecosPlantillaCorreo {

    public static final String TITULO = "{{titulo}}";
    public static final String CUERPO = "{{cuerpo}}";
    public static final String PIE = "{{pie}}";

    private static final List<String> TODOS = List.of(TITULO, CUERPO, PIE);

    // Alternacion de los tres marcadores literales, para sustituirlos en una sola pasada sin volver
    // a inspeccionar el texto ya insertado. Se deriva de TODOS para no repetir la lista.
    static final Pattern MARCADORES = Pattern.compile(
            TODOS.stream().map(Pattern::quote).collect(Collectors.joining("|")));

    private HuecosPlantillaCorreo() {}

    // String.replace de un hueco ausente no falla, solo no sustituye: sin esta comprobacion una
    // plantilla incompleta enviaria correos sin cuerpo y sin un solo error en el log.
    public static void verificar(String plantilla) {
        for (String hueco : TODOS) {
            if (plantilla == null || !plantilla.contains(hueco)) {
                throw new PlantillaCorreoNoDisponibleException(hueco);
            }
        }
    }
}
