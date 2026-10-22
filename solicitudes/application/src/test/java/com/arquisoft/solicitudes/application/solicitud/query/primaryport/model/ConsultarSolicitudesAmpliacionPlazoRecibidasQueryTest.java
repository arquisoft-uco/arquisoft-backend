package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSolicitudesAmpliacionPlazoRecibidasQueryTest {

    @Test
    void debeCrearQuery_cuandoCoordinadorUsuarioValido() {
        // Arrange
        var coordinadorUsuario = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarSolicitudesAmpliacionPlazoRecibidasQuery.crear(
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
        assertThatThrownBy(() -> ConsultarSolicitudesAmpliacionPlazoRecibidasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
