package com.arquisoft.fichas.application.estadoficha.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEstadosFichaQueryTest {

    @Test
    void debeCrearQueryConLosRoles_cuandoLaListaTieneRoles() {
        // Arrange
        var roles = List.of("ASESOR_FICHA", "REPRESENTANTE_COMITE");

        // Act
        var query = ConsultarEstadosFichaQuery.crear(roles);

        // Assert
        assertThat(query.roles()).containsExactly("ASESOR_FICHA", "REPRESENTANTE_COMITE");
    }

    @Test
    void debeCrearQueryVacia_cuandoElLlamanteNoTieneRolesReconocidos() {
        // Act
        var query = ConsultarEstadosFichaQuery.crear(List.of());

        // Assert
        assertThat(query.roles()).isEmpty();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoLosRolesSonNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaQuery.crear(null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFicha.ROLES);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EstadoFicha.ROLES_REQUERIDO);
                });
    }
}
