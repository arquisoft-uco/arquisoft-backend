package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarFichasPerfilDisponiblesEvaluacionMapperTest {

    @Test
    void debeConservarPaginacionOrdenYFiltrosYFijarElRepresentante_cuandoConvierteElQuery() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web");
        var orden = List.of(SortOrder.of("asesorNombre", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, orden, raiz);
        var query = ConsultarFichasPerfilDisponiblesEvaluacionQuery.crear(representante, criterio);

        // Act
        var criteria = ConsultarFichasPerfilDisponiblesEvaluacionMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).containsExactlyElementsOf(orden);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.getRepresentanteComite()).isEqualTo(representante);
    }

    @Test
    void debeLanzarFiltroException_cuandoElFiltroUsaEstadoFicha() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoFicha", FiltroOperador.ES, "APROBADA");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);
        var query = ConsultarFichasPerfilDisponiblesEvaluacionQuery.crear(UtilUUID.generarNuevoUUID(), criterio);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarFichasPerfilDisponiblesEvaluacionMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }
}
