package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoAsesorQuery;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarProyectosGradoAsesorMapper {

    private ConsultarProyectosGradoAsesorMapper() {}

    public static ProyectoGradoAsesorCriteria toCriteria(ConsultarProyectosGradoAsesorQuery query) {
        var criterio = query.criterio();

        var forzadoAsesor = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ASESOR.getClave(), FiltroOperador.ES,
                query.asesor().toString());

        var forzadoEstado = NodoFiltro.predicado(
                ProyectoGradoAsesorCriteria.Campo.ESTADO_ASESOR.getClave(), FiltroOperador.ES,
                EstadoAsesor.ASIGNADO.getId());

        var forzados = UtilObjeto.noEsNulo(criterio.raiz())
                ? List.of(forzadoAsesor, forzadoEstado, criterio.raiz())
                : List.of(forzadoAsesor, forzadoEstado);

        return ProyectoGradoAsesorCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento(criterio.ordenamiento()))
                .raiz(NodoFiltro.grupo(FiltroConector.AND, forzados))
                .build();
    }

    private static List<SortOrder> ordenamiento(List<SortOrder> solicitado) {
        if (!UtilColeccion.esVaciaONula(solicitado)) {
            return solicitado;
        }
        return List.of(SortOrder.of(
                ProyectoGradoAsesorCriteria.Campo.TITULO_PROYECTO.getClave(), SortDirection.ASC));
    }
}
