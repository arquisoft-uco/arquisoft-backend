package com.arquisoft.evaluaciones.domain.evaluacion.model;

import com.arquisoft.shared.message.constant.EvaluacionesCriterios;
import com.arquisoft.shared.util.UtilObjeto;

public final class CumplimientoCriterio {

    private CumplimientoCriterio() {}

    public static boolean cumple(String nombreCriterio) {
        if (UtilObjeto.esNulo(nombreCriterio)) {
            return false;
        }
        return EvaluacionesCriterios.CUMPLE.equalsIgnoreCase(nombreCriterio.strip());
    }
}
