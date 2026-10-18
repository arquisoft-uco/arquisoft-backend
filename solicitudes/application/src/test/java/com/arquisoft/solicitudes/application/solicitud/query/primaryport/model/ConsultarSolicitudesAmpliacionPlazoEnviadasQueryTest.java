package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSolicitudesAmpliacionPlazoEnviadasQueryTest {

    @Test
    void debeCrearQuery_cuandoEstudianteUsuarioValido() {
        // Arrange
        var estudianteUsuario = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarSolicitudesAmpliacionPlazoEnviadasQuery.crear(
                estudianteUsuario, criterio);

        // Assert
        assertThat(query.estudianteUsuario()).isEqualTo(estudianteUsuario);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEstudianteUsuarioEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesAmpliacionPlazoEnviadasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
