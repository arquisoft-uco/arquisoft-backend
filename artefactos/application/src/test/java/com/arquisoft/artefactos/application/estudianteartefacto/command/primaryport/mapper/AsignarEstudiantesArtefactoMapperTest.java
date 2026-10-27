package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarEstudiantesArtefactoMapperTest {

    @Test
    void debeConstruirRelacionPorCadaParArtefactoEstudiante_cuandoHayEstudiantes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var command = new AsignarEstudiantesArtefactoCommand(artefacto, asesor, List.of(primero, segundo));

        // Act
        var agregacion = AsignarEstudiantesArtefactoMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getArtefactos()).containsExactly(artefacto);
        assertThat(agregacion.getEstudiantes()).containsExactly(primero, segundo);
        assertThat(agregacion.getCantidad()).isEqualTo(2);
    }

    @Test
    void debeDevolverVacio_cuandoLaListaDeEstudiantesLlegaVaciaONula() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(UtilUUID.generarNuevoUUID());

        // Act
        var sinLista = AsignarEstudiantesArtefactoMapper.toDomain(asesor, artefactos, null);
        var conListaVacia = AsignarEstudiantesArtefactoMapper.toDomain(asesor, artefactos, List.of());

        // Assert
        assertThat(sinLista).isSameAs(AgregacionEstudiantesArtefactoDomain.VACIO);
        assertThat(conListaVacia).isSameAs(AgregacionEstudiantesArtefactoDomain.VACIO);
    }
}
