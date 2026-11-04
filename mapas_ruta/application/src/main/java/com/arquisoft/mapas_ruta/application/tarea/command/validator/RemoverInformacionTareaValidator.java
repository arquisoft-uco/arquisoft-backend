package com.arquisoft.mapas_ruta.application.tarea.command.validator;

import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public interface RemoverInformacionTareaValidator {

    void validar(RemocionTareaDomain remocion, TareaDomain tarea);
}
