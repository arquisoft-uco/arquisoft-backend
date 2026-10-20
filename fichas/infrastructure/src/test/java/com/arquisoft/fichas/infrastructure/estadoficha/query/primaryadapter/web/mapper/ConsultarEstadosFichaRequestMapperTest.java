package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosFichaRequestMapperTest {

    private static final UUID ACTOR = UUID.randomUUID();

    @ParameterizedTest
    @CsvSource({
            FichasRoles.Identidad.ASESOR_FICHA + "," + FichasRoles.Negocio.ASESOR_FICHA,
            FichasRoles.Identidad.COORDINADOR + "," + FichasRoles.Negocio.COORDINADOR,
            FichasRoles.Identidad.REPRESENTANTE_COMITE + "," + FichasRoles.Negocio.REPRESENTANTE_COMITE
    })
    void debeTraducirAlRolDeNegocio_cuandoElRolDeIdentidadEstaEnLaWhitelist(String rolIdentidad, String rolNegocio) {
        // Arrange
        var identidad = identidadConRoles(List.of(rolIdentidad));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(identidad);

        // Assert
        assertThat(query.actor()).isEqualTo(ACTOR);
        assertThat(query.rolesIdentidad()).containsExactly(rolNegocio);
    }

    @Test
    void debeUnirLosRolesSinDuplicados_cuandoElLlamanteTieneVariosRoles() {
        // Arrange
        var identidad = identidadConRoles(List.of(
                FichasRoles.Identidad.COORDINADOR,
                FichasRoles.Identidad.REPRESENTANTE_COMITE,
                FichasRoles.Identidad.COORDINADOR));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(identidad);

        // Assert
        assertThat(query.rolesIdentidad()).containsExactlyInAnyOrder(
                FichasRoles.Negocio.COORDINADOR, FichasRoles.Negocio.REPRESENTANTE_COMITE);
    }

    @Test
    void debeDescartarLosRolesDesconocidos_cuandoNoEstanEnLaWhitelist() {
        // Arrange
        var identidad = identidadConRoles(List.of(
                "estudiante", "administrador", "offline_access", FichasRoles.Identidad.ASESOR_FICHA));

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(identidad);

        // Assert
        assertThat(query.rolesIdentidad()).containsExactly(FichasRoles.Negocio.ASESOR_FICHA);
    }

    @Test
    void debeRetornarRolesVacios_cuandoLaIdentidadNoTraeRoles() {
        // Arrange
        var identidad = identidadConRoles(List.of());

        // Act
        var query = ConsultarEstadosFichaRequestMapper.toQuery(identidad);

        // Assert
        assertThat(query.rolesIdentidad()).isEmpty();
    }

    private static IdentidadActual identidadConRoles(List<String> roles) {
        return new IdentidadActual(ACTOR, "a@uco.edu.co", "a", roles, "jti", null);
    }
}
