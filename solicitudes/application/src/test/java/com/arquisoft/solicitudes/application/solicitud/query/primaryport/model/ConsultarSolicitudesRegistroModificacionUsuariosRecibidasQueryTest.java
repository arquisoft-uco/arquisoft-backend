package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQueryTest {

    @Test
    void debeCrearQuery_cuandoAdministradorUsuarioValido() {
        // Arrange
        var administradorUsuario = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery.crear(
                administradorUsuario, criterio);

        // Assert
        assertThat(query.administradorUsuario()).isEqualTo(administradorUsuario);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAdministradorUsuarioEsNulo() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
