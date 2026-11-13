package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository.EstadoDocumentacionJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoDocumentacionQueryMapperTest {

    @Test
    void debeCopiarLosTresCampos_cuandoMapeaLaEntidad() {
        // Arrange
        var entity = EstadoDocumentacionJpaQueryEntity.builder()
                .id("PENDIENTE").nombre("Pendiente").descripcion("Descripcion").build();

        // Act
        var readModel = EstadoDocumentacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo("PENDIENTE");
        assertThat(readModel.nombre()).isEqualTo("Pendiente");
        assertThat(readModel.descripcion()).isEqualTo("Descripcion");
    }
}
