package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;

import java.util.List;

public final class ConsultarEntregablesProyectoGradoJuradoMapper {

    private ConsultarEntregablesProyectoGradoJuradoMapper() {}

    public static EntregableProyectoGradoCriteria toCriteria(ConsultaCriteriaQuery query) {
        var forzado = NodoFiltro.predicadoMultivalor(
                EntregableProyectoGradoCriteria.Campo.ESTADO_ENTREGABLE.getClave(), FiltroOperador.IN,
                List.of(EstadoEntregable.DISPONIBLE_EVALUACION.getId(), EstadoEntregable.EN_EVALUACION.getId()));

        var raizFinal = FiltroCierreProyectoEntregable.combinar(List.of(forzado), query.raiz());

        return EntregableProyectoGradoCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
