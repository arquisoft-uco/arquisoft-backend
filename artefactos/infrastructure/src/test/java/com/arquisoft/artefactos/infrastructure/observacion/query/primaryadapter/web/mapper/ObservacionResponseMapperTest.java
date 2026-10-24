package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionResponseMapperTest {

    @Test
    void debeCopiarLosCincoCampos_cuandoMapea() {
        // Arrange
        var readModel = new ObservacionReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Obs", "NUEVA", "Nueva");

        // Act
        var dto = ObservacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.revisionAsesor()).isEqualTo(readModel.revisionAsesor());
        assertThat(dto.descripcion()).isEqualTo("Obs");
        assertThat(dto.estado()).isEqualTo("NUEVA");
        assertThat(dto.estadoNombre()).isEqualTo("Nueva");
    }
}
