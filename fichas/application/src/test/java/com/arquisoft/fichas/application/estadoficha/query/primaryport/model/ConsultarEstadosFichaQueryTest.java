package com.arquisoft.fichas.application.estadoficha.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEstadosFichaQueryTest {

    private static final UUID ACTOR = UUID.randomUUID();

    @Test
    void debeCrearQueryConLosRoles_cuandoLaListaTieneRoles() {
        // Arrange
        var roles = List.of("ASESOR_FICHA", "REPRESENTANTE_COMITE");

        // Act
        var query = ConsultarEstadosFichaQuery.crear(ACTOR, roles);

        // Assert
        assertThat(query.rolesIdentidad()).containsExactly("ASESOR_FICHA", "REPRESENTANTE_COMITE");
    }

    @Test
    void debeCrearQueryVacia_cuandoElLlamanteNoTieneRolesReconocidos() {
        // Act
        var query = ConsultarEstadosFichaQuery.crear(ACTOR, List.of());

        // Assert
        assertThat(query.rolesIdentidad()).isEmpty();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoLosRolesSonNulos() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaQuery.crear(ACTOR, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFicha.ROLES);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EstadoFicha.ROLES_REQUERIDO);
                });
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoElActorEsNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEstadosFichaQuery.crear(null, List.of()));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.campo()).isEqualTo(FichasFields.EstadoFicha.ACTOR);
                    assertThat(e.codigoError()).isEqualTo(FichasCodes.EstadoFicha.ACTOR_REQUERIDO);
                });
    }
}
