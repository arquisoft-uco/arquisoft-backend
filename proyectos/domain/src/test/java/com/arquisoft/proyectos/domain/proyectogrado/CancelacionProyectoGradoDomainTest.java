package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelacionProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var cancelacion = CancelacionProyectoGradoDomain.crear(proyectoGrado, coordinador, "Falta de avance");

        // Assert
        assertThat(cancelacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(cancelacion.getCoordinador()).isEqualTo(coordinador);
        assertThat(cancelacion.getMotivo()).isEqualTo("Falta de avance");
    }

    @Test
    void debeRecortarMotivo_cuandoTieneEspacios() {
        // Act
        var cancelacion = CancelacionProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "  Abandono del estudiante  ");

        // Assert
        assertThat(cancelacion.getMotivo()).isEqualTo("Abandono del estudiante");
    }

    @Test
    void debeAcumularErrores_cuandoProyectoCoordinadorYMotivoInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> CancelacionProyectoGradoDomain.crear(null, null, "  "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.MOTIVO_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoMotivoSuperaElMaximo() {
        // Arrange
        var motivo = "a".repeat(ProyectosLimits.ProyectoGrado.MOTIVO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> CancelacionProyectoGradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), motivo))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.MOTIVO_LONGITUD_MAXIMA)));
    }
}
