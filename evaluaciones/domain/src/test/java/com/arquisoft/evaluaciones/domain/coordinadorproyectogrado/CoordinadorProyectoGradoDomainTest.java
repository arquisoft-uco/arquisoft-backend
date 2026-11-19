package com.arquisoft.evaluaciones.domain.coordinadorproyectogrado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class CoordinadorProyectoGradoDomainTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Test
    void debeCrearReplica_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var replica = CoordinadorProyectoGradoDomain.crear(
                proyecto, coordinador, "Coordinador", "coordinador@uco.edu.co", OCURRIDO_EN);

        // Assert
        assertThat(replica.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(replica.getCoordinador()).isEqualTo(coordinador);
        assertThat(replica.getNombre()).isEqualTo("Coordinador");
        assertThat(replica.getEmail()).isEqualTo("coordinador@uco.edu.co");
        assertThat(replica.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(replica.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosDatosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorProyectoGradoDomain.crear(null, null, null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                                                EvaluacionesCodes.CoordinadorProyectoGrado.PROYECTO_GRADO_REQUERIDO),
                                        tuple(EvaluacionesFields.CoordinadorProyectoGrado.COORDINADOR,
                                                EvaluacionesCodes.CoordinadorProyectoGrado.COORDINADOR_REQUERIDO),
                                        tuple(EvaluacionesFields.CoordinadorProyectoGrado.NOMBRE,
                                                EvaluacionesCodes.CoordinadorProyectoGrado.NOMBRE_REQUERIDO),
                                        tuple(EvaluacionesFields.CoordinadorProyectoGrado.EMAIL,
                                                EvaluacionesCodes.CoordinadorProyectoGrado.EMAIL_REQUERIDO),
                                        tuple(EvaluacionesFields.CoordinadorProyectoGrado.OCURRIDO_EN,
                                                EvaluacionesCodes.CoordinadorProyectoGrado.OCURRIDO_EN_REQUERIDO)));
    }

    @Test
    void debeRechazarElProyectoDeCeros_cuandoSeCrea() {
        // Act & Assert
        assertThatThrownBy(() -> CoordinadorProyectoGradoDomain.crear(
                UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.generarNuevoUUID(), "N", "e@uco.edu.co", OCURRIDO_EN))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeReconstruirSinRevalidar_cuandoLosDatosSonNulos() {
        // Act
        var replica = CoordinadorProyectoGradoDomain.reconstruir(null, null, null, null, null);

        // Assert
        assertThat(replica.getNombre()).isNull();
        assertThat(CoordinadorProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }
}
