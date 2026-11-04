package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TraspasarArtefactosMapperTest {

    @Test
    void debeConstruirElTraspaso_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var traspaso = TraspasarArtefactosMapper.toDomain(proyecto, asesor);

        // Assert
        assertThat(traspaso.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(traspaso.getAsesor()).isEqualTo(asesor);
    }
}
