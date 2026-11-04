package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarArtefactoMapperTest {

    @Test
    void debeArmarElCompuesto_conElMismoArtefactoEnTodasLasRelaciones() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var command = new AsignarArtefactoCommand(proyecto, asesor, version, estudiantes);

        // Act
        var registro = AsignarArtefactoMapper.toDomain(command);

        // Assert
        var artefacto = registro.getArtefacto();
        assertThat(artefacto.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(artefacto.getAsesor()).isEqualTo(asesor);
        assertThat(artefacto.getVersionRepositorioArtefacto()).isEqualTo(version);
        assertThat(registro.getEstudiantes().getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEstudiantes().getRelaciones())
                .hasSize(2)
                .allSatisfy(relacion -> assertThat(relacion.getArtefacto()).isEqualTo(artefacto.getId()));
    }

    @Test
    void debeLanzarEstudiantesRequeridos_cuandoLaListaEstaVacia() {
        // Arrange
        var command = new AsignarArtefactoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactoMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .contains(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS));
    }

    @Test
    void debeLanzarEstudiantesDuplicados_cuandoSeRepiteUno() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();
        var command = new AsignarArtefactoCommand(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(repetido, repetido));

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactoMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .contains(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_DUPLICADOS));
    }
}
