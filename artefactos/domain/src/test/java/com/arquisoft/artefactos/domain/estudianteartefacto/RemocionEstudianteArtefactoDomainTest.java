package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemocionEstudianteArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionEstudianteArtefactoDomain.crear(artefacto, asesor, estudiante);

        // Assert
        assertThat(remocion.getArtefacto()).isEqualTo(artefacto);
        assertThat(remocion.getAsesor()).isEqualTo(asesor);
        assertThat(remocion.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanArtefactoAsesorYEstudiante() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionEstudianteArtefactoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                                ArtefactosFields.EstudianteArtefacto.ASESOR,
                                ArtefactosFields.EstudianteArtefacto.ESTUDIANTE));
    }
}
