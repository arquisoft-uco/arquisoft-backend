package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AgregacionObservacionItemDomainTest {

    @Test
    void debeConstruirAgregacion_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var observacionItem = ObservacionItemDomain.crear(revisionItem, "Observación válida");

        // Act
        var agregacion = AgregacionObservacionItemDomain.crear(observacionItem, asesorFicha);

        // Assert
        assertThat(agregacion.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(agregacion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(agregacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(agregacion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeLanzarExcepcion_cuandoObservacionItemNulo() {
        // Act
        var excepcion = catchThrowable(() -> AgregacionObservacionItemDomain.crear(null, UUID.randomUUID()));

        // Assert — corrige el defecto cosmético del intento previo: el código es el de
        // OBSERVACION_ITEM_REQUERIDO, nunca el de REVISION_ITEM_REQUERIDO
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores)
                .extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO);
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorFichaNulo() {
        // Arrange
        var observacionItem = ObservacionItemDomain.crear(UUID.randomUUID(), "Observación válida");

        // Act
        var excepcion = catchThrowable(() -> AgregacionObservacionItemDomain.crear(observacionItem, null));

        // Assert
        assertThat(excepcion).isInstanceOf(DomainValidationException.class);
        var errores = ((DomainValidationException) excepcion).getValidationResult().getErrores();
        assertThat(errores)
                .extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
    }
}
