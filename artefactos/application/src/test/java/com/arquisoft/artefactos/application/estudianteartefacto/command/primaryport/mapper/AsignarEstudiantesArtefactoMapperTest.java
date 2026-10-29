package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesArtefactoMapperTest {

    @Test
    void debeConstruirUnaRelacionPorEstudiante_cuandoHayEstudiantes() {
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
        assertThat(agregacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(agregacion.getEstudiantes()).containsExactly(primero, segundo);
        assertThat(agregacion.getCantidad()).isEqualTo(2);
    }

    @Test
    void debeArmarLaAgregacion_conLaSobrecargaDeTresArgumentos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var agregacion = AsignarEstudiantesArtefactoMapper.toDomain(artefacto, asesor, List.of(primero, segundo));

        // Assert
        assertThat(agregacion.getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(agregacion.getEstudiantes()).containsExactly(primero, segundo);
    }

    @Test
    void debeLanzarValidacion_cuandoLaListaDeEstudiantesLlegaVaciaONula() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var command = new AsignarEstudiantesArtefactoCommand(artefacto, asesor, null);

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesArtefactoMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .contains(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS));
    }
}
