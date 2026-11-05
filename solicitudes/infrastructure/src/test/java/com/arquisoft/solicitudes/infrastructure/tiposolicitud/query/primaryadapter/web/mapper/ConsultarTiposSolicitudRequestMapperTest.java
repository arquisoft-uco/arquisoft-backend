package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTiposSolicitudRequestMapperTest {

    private static IdentidadActual identidad(String... roles) {
        return new IdentidadActual(UtilUUID.generarNuevoUUID(), "u@uco.edu.co", "usuario", List.of(roles), "token", null);
    }

    @ParameterizedTest
    @CsvSource({
            UsuariosRoles.ESTUDIANTE + "," + SolicitudesRoles.Negocio.ESTUDIANTE,
            UsuariosRoles.COORDINADOR + "," + SolicitudesRoles.Negocio.COORDINADOR,
            UsuariosRoles.ASESOR + "," + SolicitudesRoles.Negocio.ASESOR,
            UsuariosRoles.ADMINISTRADOR + "," + SolicitudesRoles.Negocio.ADMINISTRADOR
    })
    void debeTraducirElRolDeIdentidadAlRolDeNegocio_cuandoEsUnRolConocido(String rolIdentidad, String rolNegocio) {
        // Arrange
        var identidad = identidad(rolIdentidad);

        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(identidad);

        // Assert
        assertThat(query.actor()).isEqualTo(identidad.id());
        assertThat(query.rolesIdentidad()).containsExactly(rolNegocio);
    }

    @Test
    void debeUnirLosRolesSinDuplicados_cuandoElLlamanteTieneVariosRoles() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(
                identidad(UsuariosRoles.ESTUDIANTE, UsuariosRoles.COORDINADOR, UsuariosRoles.ESTUDIANTE));

        // Assert
        assertThat(query.rolesIdentidad()).containsExactlyInAnyOrder(
                SolicitudesRoles.Negocio.ESTUDIANTE, SolicitudesRoles.Negocio.COORDINADOR);
    }

    @Test
    void debeDescartarLosRolesDesconocidos_cuandoNoEstanEnElMapa() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(
                identidad(UsuariosRoles.JURADO, "offline_access"));

        // Assert
        assertThat(query.rolesIdentidad()).isEmpty();
    }

    @Test
    void debeRetornarRolesVacios_cuandoLaIdentidadNoTraeRoles() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(identidad());

        // Assert
        assertThat(query.rolesIdentidad()).isEmpty();
    }
}
