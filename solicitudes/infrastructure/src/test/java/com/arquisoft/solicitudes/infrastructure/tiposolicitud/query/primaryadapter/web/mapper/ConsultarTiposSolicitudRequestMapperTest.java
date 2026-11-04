package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTiposSolicitudRequestMapperTest {

    private static IdentidadActual identidad(String... roles) {
        return new IdentidadActual(UUID.randomUUID(), "u@uco.edu.co", "usuario", List.of(roles), "token", null);
    }

    @Test
    void debeDevolverLosCuatroTiposDeEstudiante_cuandoElRolEsEstudiante() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(identidad(UsuariosRoles.ESTUDIANTE));

        // Assert
        assertThat(query.tipos()).containsExactlyInAnyOrder(
                "NOVEDAD_PARA_EL_COORDINADOR", "NOVEDAD_PARA_EL_ASESOR",
                "CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");
    }

    @Test
    void debeDevolverSoloRegistroDeUsuarios_cuandoElRolEsCoordinador() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(identidad(UsuariosRoles.COORDINADOR));

        // Assert
        assertThat(query.tipos()).containsExactly("REGISTRO_Y_MODIFICACION_DE_USUARIOS");
    }

    @Test
    void debeUnirLosTipos_cuandoCombinaRoles() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(
                identidad(UsuariosRoles.ESTUDIANTE, UsuariosRoles.COORDINADOR));

        // Assert
        assertThat(query.tipos()).hasSize(5);
    }

    @Test
    void debeDevolverVacio_cuandoElRolNoEnviaSolicitudes() {
        // Act
        var query = ConsultarTiposSolicitudRequestMapper.toQuery(identidad(UsuariosRoles.JURADO, "rol-desconocido"));

        // Assert
        assertThat(query.tipos()).isEmpty();
    }
}
