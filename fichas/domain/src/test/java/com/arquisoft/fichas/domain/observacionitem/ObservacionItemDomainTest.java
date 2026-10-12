package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class ObservacionItemDomainTest {

    @Test
    void debeCrearObservacionItem_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var observacion = "El objetivo general no cumple con el formato requerido";

        // Act
        var observacionItem = ObservacionItemDomain.crear(revisionItem, observacion);

        // Assert — el estado siempre es PENDIENTE, sin haberlo pasado como parámetro
        assertThat(observacionItem.getId()).isNotNull();
        assertThat(observacionItem.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(observacionItem.getObservacion()).isEqualTo(observacion);
        assertThat(observacionItem.getEstadoObservacionRevision()).isEqualTo(EstadoObservacionRevision.PENDIENTE);
    }

    @Test
    void debeLanzarExcepcion_cuandoRevisionItemNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemDomain.crear(null, "Observación válida"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.ObservacionItem.REVISION_ITEM);
    }

    @Test
    void debeAcumularErrores_cuandoRevisionItemNuloYObservacionNula() {
        // Act
        var excepcion = catchThrowable(() -> ObservacionItemDomain.crear(null, null));

        // Assert — Notification Pattern: una sola excepción con ambos fieldErrors acumulados
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores)
                .extracting("codigoError")
                .contains(
                        FichasCodes.ObservacionItem.REVISION_ITEM_REQUERIDO,
                        FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA);
    }

    @Test
    void debeLanzarExcepcion_cuandoObservacionEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemDomain.crear(UUID.randomUUID(), "   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA);
    }

    @Test
    void debeLanzarExcepcion_cuandoObservacionExcedeLongitudMaxima() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> ObservacionItemDomain.crear(UUID.randomUUID(), observacionDemasiadoLarga))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA);
    }
}
