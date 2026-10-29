package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DesvinculacionEstudianteArtefactosDomainTest {

    @Test
    void debeCrear_cuandoProyectoYEstudianteValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var desvinculacion = DesvinculacionEstudianteArtefactosDomain.crear(proyecto, estudiante);

        // Assert
        assertThat(desvinculacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(desvinculacion.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoAmbosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DesvinculacionEstudianteArtefactosDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.EstudianteArtefacto.PROYECTO_GRADO,
                                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE));
    }
}
