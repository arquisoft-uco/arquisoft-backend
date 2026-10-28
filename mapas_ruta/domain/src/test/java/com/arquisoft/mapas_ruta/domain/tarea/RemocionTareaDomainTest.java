package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemocionTareaDomainTest {

    @Test
    void debeCrear_cuandoTareaValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionTareaDomain.crear(tarea);

        // Assert
        assertThat(remocion.getTarea()).isEqualTo(tarea);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoTareaNula() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionTareaDomain.crear(null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.Tarea.TAREA_REQUERIDA));
    }
}
