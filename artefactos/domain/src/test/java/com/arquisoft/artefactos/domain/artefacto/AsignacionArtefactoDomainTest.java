package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignacionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoArtefactoYEstudiantesPresentes() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var artefacto = ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), asesor, UtilUUID.generarNuevoUUID());
        var estudiantes = AgregacionEstudiantesArtefactoDomain.crear(
                asesor, EstudianteArtefactoDomain.crear(artefacto.getId(), List.of(UtilUUID.generarNuevoUUID())));

        // Act
        var asignacion = AsignacionArtefactoDomain.crear(artefacto, estudiantes);

        // Assert
        assertThat(asignacion.getArtefacto()).isSameAs(artefacto);
        assertThat(asignacion.getEstudiantes()).isSameAs(estudiantes);
    }

    @Test
    void debeAcumularErrores_cuandoArtefactoYEstudiantesSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AsignacionArtefactoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.Artefacto.ID, ArtefactosFields.EstudianteArtefacto.ESTUDIANTES));
    }
}
