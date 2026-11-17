package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesAsignadasAsesorQueryTest {

    private static final ConsultaCriteriaQuery CRITERIO = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

    @Test
    void debeCrear_cuandoElAsesorEsValido() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesAsignadasAsesorQuery.crear(asesor, CRITERIO);

        // Assert
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio()).isSameAs(CRITERIO);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElAsesorEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesAsignadasAsesorQuery.crear(null, CRITERIO))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .singleElement()
                                .satisfies(error -> {
                                    assertThat(error.campo()).isEqualTo(EvaluacionesFields.EvaluacionAsesor.ASESOR);
                                    assertThat(error.codigoError())
                                            .isEqualTo(EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO);
                                }));
    }
}
