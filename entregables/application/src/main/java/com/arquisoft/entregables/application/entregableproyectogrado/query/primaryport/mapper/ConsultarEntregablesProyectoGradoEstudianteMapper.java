package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoEstudianteCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoEstudianteQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;

import java.util.List;

public final class ConsultarEntregablesProyectoGradoEstudianteMapper {

    private ConsultarEntregablesProyectoGradoEstudianteMapper() {}

    public static EntregableProyectoGradoEstudianteCriteria toCriteria(
            ConsultarEntregablesProyectoGradoEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EntregableProyectoGradoCriteria.Campo.PROYECTO_GRADO_ID.getClave(), FiltroOperador.ES,
                query.proyectoGrado().toString());

        var raizFinal = FiltroCierreProyectoEntregable.combinar(List.of(forzado), criterio.raiz());

        var consulta = EntregableProyectoGradoCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();

        return new EntregableProyectoGradoEstudianteCriteria(query.estudiante(), query.proyectoGrado(), consulta);
    }
}
