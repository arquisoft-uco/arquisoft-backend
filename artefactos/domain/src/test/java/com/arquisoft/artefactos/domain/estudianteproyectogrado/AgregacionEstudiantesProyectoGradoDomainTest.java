package com.arquisoft.artefactos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class AgregacionEstudiantesProyectoGradoDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-02T10:00:00Z");

    @Test
    void debeExponerProyectoYEstudiantes_cuandoVinculosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudianteA = UtilUUID.generarNuevoUUID();
        var estudianteB = UtilUUID.generarNuevoUUID();
        var vinculos = List.of(
                EstudianteProyectoGradoDomain.crear(estudianteA, proyecto, OCURRIDO_EN),
                EstudianteProyectoGradoDomain.crear(estudianteB, proyecto, OCURRIDO_EN));

        // Act
        var agregacion = AgregacionEstudiantesProyectoGradoDomain.crear(vinculos);

        // Assert
        assertThat(agregacion.getVinculos()).isEqualTo(vinculos);
        assertThat(agregacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(agregacion.getEstudiantes()).containsExactly(estudianteA, estudianteB);
    }

    @Test
    void debeLanzar_cuandoVinculosNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo(), e -> e.codigoError())
                        .containsExactly(tuple(
                                ArtefactosFields.EstudianteProyectoGrado.VINCULOS,
                                ArtefactosCodes.EstudianteProyectoGrado.VINCULOS_REQUERIDOS)));
    }

    @Test
    void debeLanzar_cuandoEstudiantesDuplicados() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var vinculos = List.of(
                EstudianteProyectoGradoDomain.crear(estudiante, proyecto, OCURRIDO_EN),
                EstudianteProyectoGradoDomain.crear(estudiante, proyecto, OCURRIDO_EN));

        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(vinculos))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactly(ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_DUPLICADOS));
    }
}
