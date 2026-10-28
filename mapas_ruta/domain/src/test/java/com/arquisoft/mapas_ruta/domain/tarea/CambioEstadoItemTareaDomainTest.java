package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioEstadoItemTareaDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var cambio = CambioEstadoItemTareaDomain.crear(tarea, " EN_PROGRESO ");

        // Assert
        assertThat(cambio.getTarea()).isEqualTo(tarea);
        assertThat(cambio.getEstadoItem()).isEqualTo(EstadoItem.EN_PROGRESO);
    }

    @Test
    void debeAcumularErrores_cuandoTareaNulaYEstadoEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoItemTareaDomain.crear(null, "  "))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.Tarea.TAREA_REQUERIDA,
                                        MapasRutaCodes.Tarea.ESTADO_ITEM_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoEstadoNoEsDelCatalogo() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambioEstadoItemTareaDomain.crear(tarea, "VACIO"))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.Tarea.ESTADO_ITEM_INVALIDO));
    }
}
