package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarTiposSolicitudParaRecibirQueryTest {

    @Test
    void debeCrearQueryConActorYRoles_cuandoSonValidos() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var roles = List.of("COORDINADOR", "ASESOR");

        // Act
        var query = ConsultarTiposSolicitudParaRecibirQuery.crear(actor, roles);

        // Assert
        assertThat(query.actor()).isEqualTo(actor);
        assertThat(query.rolesIdentidad()).containsExactlyElementsOf(roles);
    }

    @Test
    void debeCrearQueryConRolesVacios_cuandoElLlamanteNoTieneRolesReconocidos() {
        // Act
        var query = ConsultarTiposSolicitudParaRecibirQuery.crear(UtilUUID.generarNuevoUUID(), List.of());

        // Assert
        assertThat(query.rolesIdentidad()).isEmpty();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoLosRolesSonNulos() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ConsultarTiposSolicitudParaRecibirQuery.crear(actor, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores()).singleElement().satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(SolicitudesFields.TipoSolicitud.ROLES);
                            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.TipoSolicitud.ROLES_REQUERIDO);
                        }));
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoElActorEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarTiposSolicitudParaRecibirQuery.crear(null, List.of("COORDINADOR")))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores()).singleElement().satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(SolicitudesFields.TipoSolicitud.ACTOR);
                            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.TipoSolicitud.ACTOR_REQUERIDO);
                        }));
    }
}
