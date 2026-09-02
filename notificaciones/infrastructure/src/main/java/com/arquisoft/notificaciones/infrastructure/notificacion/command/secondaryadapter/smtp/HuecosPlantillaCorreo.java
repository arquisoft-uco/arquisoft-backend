package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;

import java.util.List;

public final class HuecosPlantillaCorreo {

    public static final String TITULO = "{{titulo}}";
    public static final String CUERPO = "{{cuerpo}}";
    public static final String PIE = "{{pie}}";

    private static final List<String> TODOS = List.of(TITULO, CUERPO, PIE);

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
