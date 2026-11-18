package com.arquisoft.entregables.application.artefacto.query.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;

public final class ConsultarVistaPreviaPaqueteMapper {

    private ConsultarVistaPreviaPaqueteMapper() {}

    public static VistaPreviaPaqueteCriteria toCriteria(ConsultarVistaPreviaPaqueteQuery query) {
        return new VistaPreviaPaqueteCriteria(query.proyectoGrado(), query.asesor());
    }
}
