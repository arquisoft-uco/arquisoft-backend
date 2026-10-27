package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
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
        var command = new AsignarArtefactosCommand(proyecto, asesor, List.of(primera, segunda), List.of());

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
        assertThat(asignacion.getEstudiantes()).isSameAs(AgregacionEstudiantesArtefactoDomain.VACIO);
    }

    @Test
    void debeRelacionarCadaEstudianteConCadaArtefactoGenerado_cuandoHayEstudiantes() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var command = new AsignarArtefactosCommand(
                UtilUUID.generarNuevoUUID(), asesor,
                List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()), List.of(primero, segundo));

        // Act
        var asignacion = AsignarArtefactosMapper.toDomain(command);

        // Assert
        var idsGenerados = asignacion.getArtefactos().stream().map(ArtefactoDomain::getId).toList();
        assertThat(asignacion.getEstudiantes().esVacio()).isFalse();
        assertThat(asignacion.getEstudiantes().getAsesor()).isEqualTo(asesor);
        assertThat(asignacion.getEstudiantes().getArtefactos()).containsExactlyElementsOf(idsGenerados);
        assertThat(asignacion.getEstudiantes().getEstudiantes()).containsExactly(primero, segundo);
        assertThat(asignacion.getEstudiantes().getCantidad()).isEqualTo(4);
    }
}
