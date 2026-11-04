package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarObservacionesEvaluacionCoordinadorQueryTest {

    @Test
    void debeCrearQuery_cuandoFichaEsValida() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesEvaluacionCoordinadorQuery.crear(fichaPerfil);

        // Assert
        assertThat(query.fichaPerfil()).isEqualTo(fichaPerfil);
    }

    @Test
    void debeLanzarValidacion_cuandoFichaEsNula() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarObservacionesEvaluacionCoordinadorQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.ObservacionEvaluacion.FICHA_PERFIL);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.ObservacionEvaluacion.FICHA_PERFIL_REQUERIDA);
                });
    }
}
