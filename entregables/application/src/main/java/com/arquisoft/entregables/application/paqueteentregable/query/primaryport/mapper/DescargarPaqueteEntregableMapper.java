package com.arquisoft.entregables.application.paqueteentregable.query.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.criteria.DescargaPaqueteEntregableCriteria;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.DescargarPaqueteEntregableQuery;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.RolConsultaPaqueteEntregable;

public final class DescargarPaqueteEntregableMapper {

    private DescargarPaqueteEntregableMapper() {}

    public static DescargaPaqueteEntregableCriteria toCriteria(DescargarPaqueteEntregableQuery query) {
        return new DescargaPaqueteEntregableCriteria(
                query.entregableProyectoGrado(),
                query.actor(),
                query.roles().contains(RolConsultaPaqueteEntregable.ASESOR),
                query.roles().contains(RolConsultaPaqueteEntregable.ESTUDIANTE));
    }
}
