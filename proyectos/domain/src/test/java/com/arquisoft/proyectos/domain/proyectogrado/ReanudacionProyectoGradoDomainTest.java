package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReanudacionProyectoGradoDomainTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 19);

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var fechaFin = HOY.plusDays(10);

        // Act
        var reanudacion = ReanudacionProyectoGradoDomain.crear(proyectoGrado, fechaFin, HOY);

        // Assert
        assertThat(reanudacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(reanudacion.getFechaFin()).isEqualTo(fechaFin);
        assertThat(reanudacion.getHoy()).isEqualTo(HOY);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> ReanudacionProyectoGradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                        ProyectosCodes.ProyectoGrado.FECHA_FIN_REQUERIDA,
                                        ProyectosCodes.ProyectoGrado.HOY_REQUERIDA));
    }

    @Test
    void debeIndicarPlazoVigente_segunLaFechaFinFrenteAHoy() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var ayer = ReanudacionProyectoGradoDomain.crear(proyectoGrado, HOY.minusDays(1), HOY);
        var hoy = ReanudacionProyectoGradoDomain.crear(proyectoGrado, HOY, HOY);
        var manana = ReanudacionProyectoGradoDomain.crear(proyectoGrado, HOY.plusDays(1), HOY);

        // Assert
        assertThat(ayer.plazoVigente()).isFalse();
        assertThat(hoy.plazoVigente()).isTrue();
        assertThat(manana.plazoVigente()).isTrue();
    }
}
