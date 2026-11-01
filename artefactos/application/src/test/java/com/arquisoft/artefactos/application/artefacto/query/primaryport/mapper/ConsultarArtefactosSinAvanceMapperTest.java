package com.arquisoft.artefactos.application.artefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarArtefactosSinAvanceMapperTest {

    @Test
    void debeFijarAsesorYCalcularFechaLimite_cuandoToCriteria() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarArtefactosSinAvanceQuery.crear(asesor, 30, criterio);
        var antes = Instant.now();

        // Act
        var criteria = ConsultarArtefactosSinAvanceMapper.toCriteria(query);

        // Assert
        var despues = Instant.now();
        assertThat(criteria.getAsesor()).isEqualTo(asesor);
        assertThat(criteria.getFechaLimite())
                .isBetween(antes.minus(30, ChronoUnit.DAYS).minus(Duration.ofSeconds(1)),
                        despues.minus(30, ChronoUnit.DAYS).plus(Duration.ofSeconds(1)));
        assertThat(criteria.getRaiz()).isNull();
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrdenamiento() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarArtefactosSinAvanceQuery.crear(UtilUUID.generarNuevoUUID(), 30, criterio);

        // Act
        var criteria = ConsultarArtefactosSinAvanceMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(2);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(ArtefactoSinAvanceCriteria.Campo.NOMBRE_PROYECTO.getClave());
        assertThat(criteria.getOrdenamiento().get(1).getCampo())
                .isEqualTo(ArtefactoSinAvanceCriteria.Campo.NOMBRE_DOCUMENTO.getClave());
        assertThat(criteria.getOrdenamiento()).allSatisfy(o ->
                assertThat(o.getDireccion()).isEqualTo(SortDirection.ASC));
    }

    @Test
    void debeConservarPaginaTamanioYOrdenDelCliente_cuandoLoEnvia() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("fechaUltimaVersion", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarArtefactosSinAvanceQuery.crear(UtilUUID.generarNuevoUUID(), 30, criterio);

        // Act
        var criteria = ConsultarArtefactosSinAvanceMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaUltimaVersion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
