package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarArtefactosMapperTest {

    @Test
    void debeConstruirUnArtefactoPorVersionCompartiendoProyectoYAsesor() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var command = new AsignarArtefactosCommand(proyecto, asesor, List.of(primera, segunda));

        // Act
        var asignacion = AsignarArtefactosMapper.toDomain(command);

        // Assert
        assertThat(asignacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(asignacion.getAsesor()).isEqualTo(asesor);
        assertThat(asignacion.getArtefactos()).hasSize(2);
        assertThat(asignacion.getVersionesRepositorioArtefacto()).containsExactly(primera, segunda);
        assertThat(asignacion.getArtefactos()).allSatisfy(artefacto -> {
            assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
            assertThat(artefacto.getAsesor()).isEqualTo(asesor);
        });
        assertThat(asignacion.getArtefactos().get(0).getId())
                .isNotEqualTo(asignacion.getArtefactos().get(1).getId());
    }
}
