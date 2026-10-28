package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class AgregacionEstudiantesArtefactoDomainTest {

    @Test
    void debeExponerArtefactoYEstudiantes_cuandoHayVarios() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudianteA = UtilUUID.generarNuevoUUID();
        var estudianteB = UtilUUID.generarNuevoUUID();
        var relaciones = EstudianteArtefactoDomain.crear(artefacto, List.of(estudianteA, estudianteB));

        // Act
        var agregacion = AgregacionEstudiantesArtefactoDomain.crear(asesor, relaciones);

        // Assert
        assertThat(agregacion.getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getCantidad()).isEqualTo(2);
        assertThat(agregacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(agregacion.getEstudiantes()).containsExactly(estudianteA, estudianteB);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanAsesorYRelaciones() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesArtefactoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactlyInAnyOrder(
                                tuple(ArtefactosFields.EstudianteArtefacto.ASESOR,
                                        ArtefactosCodes.EstudianteArtefacto.ASESOR_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                                        ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS)));
    }
}
