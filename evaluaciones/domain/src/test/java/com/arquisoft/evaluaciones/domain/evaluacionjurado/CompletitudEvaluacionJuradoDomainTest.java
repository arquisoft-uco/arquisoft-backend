package com.arquisoft.evaluaciones.domain.evaluacionjurado;

import com.arquisoft.evaluaciones.domain.estadoevaluacionasignadajurado.EstadoEvaluacionAsignadaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompletitudEvaluacionJuradoDomainTest {

    @Test
    void debeRequerirTransicionAEvaluada_cuandoEstaCompleta() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();

        // Act
        var completitud = CompletitudEvaluacionJuradoDomain.crear(evaluacionJurado, true);

        // Assert
        assertThat(completitud.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(completitud.isCompleta()).isTrue();
        assertThat(completitud.requiereTransicion()).isTrue();
        assertThat(completitud.estadoDestino()).isEqualTo(EstadoEvaluacionAsignadaJurado.EVALUADA);
    }

    @Test
    void debeNoRequerirTransicion_cuandoNoEstaCompleta() {
        // Act
        var completitud = CompletitudEvaluacionJuradoDomain.crear(UtilUUID.generarNuevoUUID(), false);

        // Assert
        assertThat(completitud.requiereTransicion()).isFalse();
    }

    @Test
    void debeLanzarValidacion_cuandoLaEvaluacionJuradoEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> CompletitudEvaluacionJuradoDomain.crear(null, true))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .singleElement()
                                .satisfies(error -> {
                                    assertThat(error.campo()).isEqualTo(EvaluacionesFields.EvaluacionJurado.ID);
                                    assertThat(error.codigoError())
                                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.ID_REQUERIDO);
                                }));
    }
}
