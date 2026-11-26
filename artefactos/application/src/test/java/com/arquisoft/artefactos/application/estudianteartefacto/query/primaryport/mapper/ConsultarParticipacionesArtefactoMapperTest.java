package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
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

class ConsultarParticipacionesArtefactoMapperTest {

    @Test
    void debeForzarSoloElPredicadoDelEstudiante_cuandoQueryNoTraeFiltrosDelCliente() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio);

        // Act
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(ParticipacionArtefactoCriteria.Campo.ESTUDIANTE_ID.getClave(),
                        FiltroOperador.ES, estudiante.toString()));
    }

    @Test
    void debeCombinarConAndConElForzadoPrimero_aunqueElClienteFiltrePorOtroEstudianteId() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var otroEstudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado(ParticipacionArtefactoCriteria.Campo.ESTUDIANTE_ID.getClave(),
                FiltroOperador.ES, otroEstudiante.toString());
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio);
        var forzado = NodoFiltro.predicado(ParticipacionArtefactoCriteria.Campo.ESTUDIANTE_ID.getClave(),
                FiltroOperador.ES, estudiante.toString());

        // Act
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio);

        // Act
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(2);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(ParticipacionArtefactoCriteria.Campo.NOMBRE_PROYECTO.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
        assertThat(criteria.getOrdenamiento().get(1).getCampo())
                .isEqualTo(ParticipacionArtefactoCriteria.Campo.NOMBRE_DOCUMENTO.getClave());
        assertThat(criteria.getOrdenamiento().get(1).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeConservarPaginaTamanioYOrden_cuandoElClienteLoEnvia() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("estadoUltimaRevision", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante, criterio);

        // Act
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estadoUltimaRevision");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
