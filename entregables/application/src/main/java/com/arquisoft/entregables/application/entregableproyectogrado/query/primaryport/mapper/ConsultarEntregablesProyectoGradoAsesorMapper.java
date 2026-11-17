package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;

import java.util.List;

public final class ConsultarEntregablesProyectoGradoAsesorMapper {

    private ConsultarEntregablesProyectoGradoAsesorMapper() {}

    public static EntregableProyectoGradoCriteria toCriteria(ConsultarEntregablesProyectoGradoAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EntregableProyectoGradoCriteria.Campo.ASESOR_VIGENTE_ID.getClave(), FiltroOperador.ES,
                query.asesor().toString());

        var raizFinal = FiltroCierreProyectoEntregable.combinar(List.of(forzado), criterio.raiz());

        return EntregableProyectoGradoCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
