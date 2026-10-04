package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSolicitudesNovedadAsesorRecibidasQueryTest {

    @Test
    void debeCrearQuery_cuandoAsesorUsuarioValido() {
        // Arrange
        var asesorUsuario = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarSolicitudesNovedadAsesorRecibidasQuery.crear(asesorUsuario, criterio);

        // Assert
        assertThat(query.asesorUsuario()).isEqualTo(asesorUsuario);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAsesorUsuarioEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesNovedadAsesorRecibidasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
