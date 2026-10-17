package com.arquisoft.solicitudes.application.respuesta.query.primaryport.model;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarRespuestasNovedadCoordinadorEnviadasQueryTest {

    @Test
    void debeCrearQuery_cuandoCoordinadorUsuarioValido() {
        // Arrange
        var coordinadorUsuario = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarRespuestasNovedadCoordinadorEnviadasQuery.crear(
                coordinadorUsuario, criterio);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoCoordinadorUsuarioEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarRespuestasNovedadCoordinadorEnviadasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
