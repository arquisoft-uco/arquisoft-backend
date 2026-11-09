package com.arquisoft.entregables.application.paqueteentregable.query.validator;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

public interface DescargarPaqueteEntregableValidator {

    void validar(DescargaPaqueteEntregableCriteria criteria, EntregableProyectoGradoDomain entregable,
                 boolean participa, PaqueteEntregableDomain paquete);
}
