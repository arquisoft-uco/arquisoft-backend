package com.arquisoft.entregables.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionEstudiantesProyectoGradoDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Test
    void debeConservarProyectoYVinculos_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculos = List.of(
                EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyectoGrado, OCURRIDO_EN),
                EstudianteProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), proyectoGrado, OCURRIDO_EN));

        // Act
        var agregacion = AgregacionEstudiantesProyectoGradoDomain.crear(proyectoGrado, vinculos);

        // Assert
        assertThat(agregacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(agregacion.getVinculos()).containsExactlyElementsOf(vinculos);
    }

    @Test
    void debeRechazarListaVacia_cuandoNoHayVinculos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculos = List.<EstudianteProyectoGradoDomain>of();

        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(proyectoGrado, vinculos))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactly(EntregablesCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS));
    }

    @Test
    void debeAcumularErrores_cuandoProyectoYVinculosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.codigoError())
                        .containsExactlyInAnyOrder(
                                EntregablesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                EntregablesCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS));
    }
}
