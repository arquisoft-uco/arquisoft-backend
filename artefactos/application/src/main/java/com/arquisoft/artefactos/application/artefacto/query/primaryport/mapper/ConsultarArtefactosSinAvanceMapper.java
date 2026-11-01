package com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilFecha;

import java.time.temporal.ChronoUnit;
import java.util.List;

public final class ConsultarArtefactosSinAvanceMapper {

    private ConsultarArtefactosSinAvanceMapper() {}

    public static ArtefactoSinAvanceCriteria toCriteria(ConsultarArtefactosSinAvanceQuery query) {
        var criterio = query.criterio();

        var fechaLimite = UtilFecha.generarInstanteActual().minus(query.diasSinAvance(), ChronoUnit.DAYS);

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(
                        SortOrder.of(ArtefactoSinAvanceCriteria.Campo.NOMBRE_PROYECTO.getClave(), SortDirection.ASC),
                        SortOrder.of(ArtefactoSinAvanceCriteria.Campo.NOMBRE_DOCUMENTO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ArtefactoSinAvanceCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .asesor(query.asesor())
                .fechaLimite(fechaLimite)
                .build();
    }
}
