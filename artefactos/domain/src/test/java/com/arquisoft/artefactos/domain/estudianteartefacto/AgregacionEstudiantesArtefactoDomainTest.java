package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
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
    void debeExponerParesYDistintos_cuandoHayVariosArtefactosYEstudiantes() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var artefactoA = UtilUUID.generarNuevoUUID();
        var artefactoB = UtilUUID.generarNuevoUUID();
        var estudianteA = UtilUUID.generarNuevoUUID();
        var estudianteB = UtilUUID.generarNuevoUUID();
        var relaciones = EstudianteArtefactoDomain.crear(List.of(artefactoA, artefactoB), List.of(estudianteA, estudianteB));

        // Act
        var agregacion = AgregacionEstudiantesArtefactoDomain.crear(asesor, relaciones);

        // Assert
        assertThat(agregacion.getAsesor()).isEqualTo(asesor);
        assertThat(agregacion.getCantidad()).isEqualTo(4);
        assertThat(agregacion.getArtefactos()).containsExactly(artefactoA, artefactoB);
        assertThat(agregacion.getEstudiantes()).containsExactly(estudianteA, estudianteB);
        assertThat(agregacion.getPares()).containsExactlyInAnyOrder(
                new ParEstudianteArtefacto(artefactoA, estudianteA), new ParEstudianteArtefacto(artefactoA, estudianteB),
                new ParEstudianteArtefacto(artefactoB, estudianteA), new ParEstudianteArtefacto(artefactoB, estudianteB));
        assertThat(agregacion.esVacio()).isFalse();
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

    @Test
    void debeSerVacioYSinElementos_cuandoSeUsaElCentinela() {
        // Act
        var vacio = AgregacionEstudiantesArtefactoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(vacio.getRelaciones()).isEmpty();
        assertThat(vacio.getPares()).isEmpty();
        assertThat(vacio.getCantidad()).isZero();
    }
}
