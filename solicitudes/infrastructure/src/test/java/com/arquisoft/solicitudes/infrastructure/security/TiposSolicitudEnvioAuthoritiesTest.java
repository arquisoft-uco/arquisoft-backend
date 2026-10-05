package com.arquisoft.solicitudes.infrastructure.security;

import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TiposSolicitudEnvioAuthoritiesTest {

    private static List<GrantedAuthority> authorities(String... nombres) {
        return Stream.of(nombres).<GrantedAuthority>map(SimpleGrantedAuthority::new).toList();
    }

    @Test
    void debeRetornarLosCuatroTipos_cuandoEsEstudiante() {
        // Arrange
        var concedidas = authorities(
                SolicitudesAuthorities.SOLICITUD_CREATE,
                SolicitudesAuthorities.SOLICITUD_NOVEDAD_ASESOR_CREATE,
                SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE,
                SolicitudesAuthorities.SOLICITUD_AMPLIACION_PLAZO_CREATE);

        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(concedidas);

        // Assert
        assertThat(resultado).containsExactlyInAnyOrder(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId(),
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR.getId(),
                TipoSolicitud.CAMBIO_DE_ASESOR.getId(),
                TipoSolicitud.AMPLIACION_DE_PLAZO.getId());
    }

    @Test
    void debeRetornarSoloRegistroDeUsuarios_cuandoEsCoordinador() {
        // Arrange
        var concedidas = authorities(SolicitudesAuthorities.SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_CREATE);

        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(concedidas);

        // Assert
        assertThat(resultado).containsExactly(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS.getId());
    }

    @Test
    void debeRetornarVacio_cuandoNoHayAuthorities() {
        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(List.of());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoLasAuthoritiesSonAjenasAlEnvio() {
        // Arrange
        var concedidas = authorities(
                SolicitudesAuthorities.TIPO_SOLICITUD_VIEW,
                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_DELETE,
                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDA_VIEW);

        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(concedidas);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarSoloLosDelSubconjunto_cuandoTieneAlgunosRoles() {
        // Arrange
        var concedidas = authorities(
                SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE,
                SolicitudesAuthorities.TIPO_SOLICITUD_VIEW);

        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(concedidas);

        // Assert
        assertThat(resultado).containsExactly(TipoSolicitud.CAMBIO_DE_ASESOR.getId());
    }

    @Test
    void debeCoincidirConElCatalogoDeDominio_cuandoTieneTodosLosRolesDeEnvio() {
        // Arrange
        var concedidas = authorities(
                SolicitudesAuthorities.SOLICITUD_CREATE,
                SolicitudesAuthorities.SOLICITUD_NOVEDAD_ASESOR_CREATE,
                SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE,
                SolicitudesAuthorities.SOLICITUD_AMPLIACION_PLAZO_CREATE,
                SolicitudesAuthorities.SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_CREATE);

        // Act
        var resultado = TiposSolicitudEnvioAuthorities.tiposPermitidos(concedidas);

        // Assert
        var catalogo = Arrays.stream(TipoSolicitud.values())
                .filter(tipo -> tipo != TipoSolicitud.VACIO)
                .map(TipoSolicitud::getId)
                .toList();
        assertThat(resultado).containsExactlyInAnyOrderElementsOf(catalogo);
    }
}
