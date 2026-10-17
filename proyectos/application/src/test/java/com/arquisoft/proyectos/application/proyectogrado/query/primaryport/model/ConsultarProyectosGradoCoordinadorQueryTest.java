package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarProyectosGradoCoordinadorQueryTest {

    @Test
    void debeCrearQuery_cuandoCoordinadorValido() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarProyectosGradoCoordinadorQuery.crear(coordinador, criterio);

        // Assert
        assertThat(query.coordinador()).isEqualTo(coordinador);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarValidacion_cuandoCoordinadorNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarProyectosGradoCoordinadorQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactly(ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO));
    }
}
