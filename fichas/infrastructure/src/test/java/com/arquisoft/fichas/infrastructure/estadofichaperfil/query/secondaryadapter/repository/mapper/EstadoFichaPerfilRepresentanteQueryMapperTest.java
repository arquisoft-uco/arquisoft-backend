package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilRepresentanteJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaPerfilRepresentanteQueryMapperTest {

    @Test
    void debeMapearJpaQueryEntityAReadModel_cuandoSeConvierte() {
        // Arrange
        var fechaActualizacion = Instant.now();
        var entity = EstadoFichaPerfilRepresentanteJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .fichaPerfilId(UtilUUID.generarNuevoUUID())
                .representanteComiteId(UtilUUID.generarNuevoUUID())
                .estadoId("DISPONIBLE_PARA_EVALUACION")
                .estadoNombre("Disponible Para Evaluacion")
                .fechaActualizacion(fechaActualizacion)
                .build();

        // Act
        var readModel = EstadoFichaPerfilRepresentanteQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo("DISPONIBLE_PARA_EVALUACION");
        assertThat(readModel.nombre()).isEqualTo("Disponible Para Evaluacion");
        assertThat(readModel.fechaActualizacion()).isEqualTo(fechaActualizacion);
    }
}
