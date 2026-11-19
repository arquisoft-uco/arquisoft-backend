package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizacionEstadoProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var actualizacion = ActualizacionEstadoProyectoGradoDomain.crear(proyecto, " ATRASADO ", ocurridoEn);

        // Assert
        assertThat(actualizacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(actualizacion.getEstadoProyecto()).isEqualTo(EstadoProyectoGrado.ATRASADO);
        assertThat(actualizacion.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularFieldErrors_cuandoEstadoInvalidoYSinOcurridoEn() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ActualizacionEstadoProyectoGradoDomain.crear(proyecto, "INEXISTENTE", null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.ProyectoGrado.ESTADO_NO_ENCONTRADO,
                                        MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }
}
