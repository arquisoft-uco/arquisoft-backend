package com.arquisoft.evaluaciones.domain.proyectoestudianteacceso;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ProyectoEstudianteAccesoDomainTest {

    @Test
    void debeCrearConActivoTrueOFalse_segunSeaAsignacionODestitucion() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        ProyectoEstudianteAccesoDomain asignacion =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, true, ocurridoEn);
        ProyectoEstudianteAccesoDomain destitucion =
                ProyectoEstudianteAccesoDomain.crear(proyecto, estudiante, false, ocurridoEn);

        // Assert
        assertThat(asignacion.isActivo()).isTrue();
        assertThat(destitucion.isActivo()).isFalse();
        assertThat(asignacion.getProyecto()).isEqualTo(proyecto);
        assertThat(asignacion.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeReconstruir_sinRevalidar() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        ProyectoEstudianteAccesoDomain acceso =
                ProyectoEstudianteAccesoDomain.reconstruir(proyecto, estudiante, true, ocurridoEn);

        // Assert
        assertThat(acceso.getProyecto()).isEqualTo(proyecto);
        assertThat(acceso.getEstudiante()).isEqualTo(estudiante);
        assertThat(acceso.isActivo()).isTrue();
        assertThat(acceso.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void esMasRecienteQueDebeSerVerdadero_cuandoElExistenteEsVacioOEsPosteriorEnElTiempo() {
        // Arrange
        Instant ahora = Instant.now();
        ProyectoEstudianteAccesoDomain masViejo = ProyectoEstudianteAccesoDomain.reconstruir(
                UUID.randomUUID(), UUID.randomUUID(), true, ahora.minus(1, ChronoUnit.DAYS));
        ProyectoEstudianteAccesoDomain nuevo =
                ProyectoEstudianteAccesoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), false, ahora);

        // Act & Assert
        assertThat(nuevo.esMasRecienteQue(ProyectoEstudianteAccesoDomain.VACIO)).isTrue();
        assertThat(nuevo.esMasRecienteQue(masViejo)).isTrue();
    }

    @Test
    void esMasRecienteQueDebeSerFalso_cuandoElEventoEsAnteriorOTieneElMismoInstante() {
        // Arrange
        Instant ahora = Instant.now();
        ProyectoEstudianteAccesoDomain existente =
                ProyectoEstudianteAccesoDomain.reconstruir(UUID.randomUUID(), UUID.randomUUID(), true, ahora);
        ProyectoEstudianteAccesoDomain anterior = ProyectoEstudianteAccesoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), false, ahora.minus(1, ChronoUnit.DAYS));
        ProyectoEstudianteAccesoDomain duplicado =
                ProyectoEstudianteAccesoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), false, ahora);

        // Act & Assert
        assertThat(anterior.esMasRecienteQue(existente)).isFalse();
        assertThat(duplicado.esMasRecienteQue(existente)).isFalse();
    }

    @Test
    void debeAcumularTodosLosFieldErrors_cuandoProyectoEstudianteYOcurridoEnSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoEstudianteAccesoDomain.crear(null, null, true, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(
                                                EvaluacionesFields.ProyectoEstudianteAcceso.PROYECTO,
                                                EvaluacionesCodes.ProyectoEstudianteAcceso.PROYECTO_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.ProyectoEstudianteAcceso.ESTUDIANTE,
                                                EvaluacionesCodes.ProyectoEstudianteAcceso.ESTUDIANTE_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.ProyectoEstudianteAcceso.OCURRIDO_EN,
                                                EvaluacionesCodes.ProyectoEstudianteAcceso.OCURRIDO_EN_REQUERIDO)));
    }
}
