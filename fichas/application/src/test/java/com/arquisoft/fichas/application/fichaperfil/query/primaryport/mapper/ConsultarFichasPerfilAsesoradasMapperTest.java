package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarFichasPerfilAsesoradasMapperTest {

    @Test
    void debeConvertirQueryAFichaPerfilCriteria_forzandoFiltroAsesorId_cuandoQueryNoTieneFiltros() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);

        // Act
        FichaPerfilCriteria criteria = ConsultarFichasPerfilAsesoradasMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(FichaPerfilCriteria.Campo.ASESOR_ID.getClave(),
                        FiltroOperador.ES, asesorFicha.toString()));
    }

    @Test
    void debeCombinarConAndElFiltroForzadoConElRaizDelCliente_cuandoQueryTieneFiltrosPropios() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);

        var forzado = NodoFiltro.predicado(FichaPerfilCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, asesorFicha.toString());

        // Act
        FichaPerfilCriteria criteria = ConsultarFichasPerfilAsesoradasMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("tituloProyecto", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);

        // Act
        FichaPerfilCriteria criteria = ConsultarFichasPerfilAsesoradasMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("tituloProyecto");
    }
}
