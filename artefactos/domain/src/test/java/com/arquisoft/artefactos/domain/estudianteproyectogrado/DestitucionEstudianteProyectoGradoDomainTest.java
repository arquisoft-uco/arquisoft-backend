package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestitucionEstudianteProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoVinculoYDesvinculacionValidos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var vinculo = EstudianteProyectoGradoDomain.crear(estudiante, proyecto, Instant.now());
        var desvinculacion = DesvinculacionEstudianteArtefactosDomain.crear(proyecto, estudiante);

        // Act
        var destitucion = DestitucionEstudianteProyectoGradoDomain.crear(vinculo, desvinculacion);

        // Assert
        assertThat(destitucion.getVinculo()).isSameAs(vinculo);
        assertThat(destitucion.getDesvinculacion()).isSameAs(desvinculacion);
    }

    @Test
    void debeAcumularErrores_cuandoLasDosPiezasSonNulas() {
        // Act & Assert
        assertThatThrownBy(() -> DestitucionEstudianteProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.EstudianteProyectoGrado.VINCULO,
                                ArtefactosFields.EstudianteProyectoGrado.DESVINCULACION));
    }
}
