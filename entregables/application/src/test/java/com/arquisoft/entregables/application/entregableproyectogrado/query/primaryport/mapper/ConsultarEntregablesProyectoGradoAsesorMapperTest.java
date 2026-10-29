package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEntregablesProyectoGradoAsesorMapperTest {

    @Test
    void debeForzarElAsesor_cuandoNoHayRaiz() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEntregablesProyectoGradoAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        var esperado = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesor.toString());
        assertThat(criteria.getRaiz()).isEqualTo(esperado);
    }

    @Test
    void debeCombinarConAnd_cuandoHayRaizDelCliente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ana");
        var query = ConsultarEntregablesProyectoGradoAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        var forzado = NodoFiltro.predicado("asesorId", FiltroOperador.ES, asesor.toString());
        var esperado = NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raiz));
        assertThat(criteria.getRaiz()).isEqualTo(esperado);
    }

    @Test
    void debeCopiarPaginaTamanioYOrden() {
        // Arrange
        var query = ConsultarEntregablesProyectoGradoAsesorQuery.crear(
                UtilUUID.generarNuevoUUID(),
                ConsultaCriteriaQuery.crear(
                        2, 25, List.of(SortOrder.of("proyectoGradoNombre", SortDirection.DESC)), null));

        // Act
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("proyectoGradoNombre");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
