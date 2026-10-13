package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ModificacionObservacionItemDomainTest {

    @Test
    void debeCrearModificacion_cuandoDatosValidos() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionObservacionItemDomain.crear(observacionItem, "Observación válida", asesorFicha);

        // Assert
        assertThat(modificacion.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(modificacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(modificacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeRecortarAntesDeValidarYMedir_cuandoLaObservacionTraeEspacios() {
        // Arrange
        var observacionEnElLimite = "x".repeat(200);

        // Act
        var modificacion = ModificacionObservacionItemDomain.crear(
                UtilUUID.generarNuevoUUID(), "  " + observacionEnElLimite + "  ", UtilUUID.generarNuevoUUID());

        // Assert — con espacios mide 204: solo es válida si se recorta antes de medir la longitud
        assertThat(modificacion.getObservacion()).isEqualTo(observacionEnElLimite);
    }

    @Test
    void debeLanzarObservacionRequerida_cuandoLaObservacionEstaEnBlanco() {
        // Act
        var excepcion = catchThrowable(() -> ModificacionObservacionItemDomain.crear(
                UtilUUID.generarNuevoUUID(), "   ", UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA);
    }

    @Test
    void debeLanzarObservacionDemasiadoLarga_cuandoExcedeLosDoscientosCaracteres() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(201);

        // Act
        var excepcion = catchThrowable(() -> ModificacionObservacionItemDomain.crear(
                UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act
        var excepcion = catchThrowable(() -> ModificacionObservacionItemDomain.crear(null, "   ", null));

        // Assert — Notification Pattern: una sola excepción con todos los fieldErrors
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores).hasSize(3);
        assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO,
                FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA,
                FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
        assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                FichasFields.ObservacionItem.OBSERVACION_ITEM,
                FichasFields.ObservacionItem.OBSERVACION,
                FichasFields.ObservacionItem.ASESOR_FICHA);
    }
}
