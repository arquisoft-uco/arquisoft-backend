package com.arquisoft.mapas_ruta.application.tarea.command.validator;

import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public interface CambiarEstadoItemTareaValidator {

    void validar(CambioEstadoItemTareaDomain cambio, TareaDomain tarea);
}
