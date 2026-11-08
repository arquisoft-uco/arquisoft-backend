package com.arquisoft.entregables.application.paqueteentregable.query.validator;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

import java.util.UUID;

public interface DescargarPaqueteEntregableValidator {

    void validar(DescargaPaqueteEntregableCriteria criteria, UUID asesor, boolean participa,
                 PaqueteEntregableDomain paquete);
}
