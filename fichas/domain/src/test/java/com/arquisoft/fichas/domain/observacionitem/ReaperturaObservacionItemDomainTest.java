package com.arquisoft.fichas.domain.observacionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReaperturaObservacionItemDomainTest {

    @Test
    void debeAsignarAmbosCampos_cuandoDatosValidos() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var reapertura = ReaperturaObservacionItemDomain.crear(observacionItem, asesorFicha);

        // Assert
        assertThat(reapertura.getObservacionItem()).isEqualTo(observacionItem);
        assertThat(reapertura.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoObservacionItemYAsesorFichaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ReaperturaObservacionItemDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.ObservacionItem.OBSERVACION_ITEM,
                                    FichasFields.ObservacionItem.ASESOR_FICHA);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO,
                                    FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
                });
    }
}
