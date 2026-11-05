package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeleccionProveedorTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=abc";
    private static final String TOKEN = "tok-1";

    private ProveedorAutenticacion bearer;
    private ProveedorAutenticacion sesion;
    private ProveedorAutenticacion apagado;
    private ProveedoresAutenticacion registro;
    private MockHttpServletRequest request;

    @BeforeEach
    void preparar() {
        bearer = mock(ProveedorAutenticacion.class);
        when(bearer.id()).thenReturn("bearer");
        when(bearer.habilitado()).thenReturn(true);
        when(bearer.actuaEnRutasNoDeNegocio()).thenReturn(true);
        sesion = mock(ProveedorAutenticacion.class);
        when(sesion.id()).thenReturn("sesion-pdp");
        when(sesion.habilitado()).thenReturn(true);
        when(sesion.actuaEnRutasNoDeNegocio()).thenReturn(false);
        apagado = mock(ProveedorAutenticacion.class);
        when(apagado.id()).thenReturn("apagado");
        when(apagado.habilitado()).thenReturn(false);
        when(bearer.emisorHabilitado("legado")).thenReturn(Optional.of("legado"));
        when(bearer.emisorHabilitado("LEGADO")).thenReturn(Optional.of("legado"));
        when(bearer.declaraEmisor("inactivo")).thenReturn(true);
        registro = new ProveedoresAutenticacion(List.of(bearer, sesion, apagado));
        request = new MockHttpServletRequest("POST", "/api/fichas-perfil");
    }

    private void conCookie() {
        when(sesion.extraerCredencial(any())).thenReturn(Optional.of(COOKIE));
    }

    private void conBearer() {
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of(TOKEN));
    }

    private void sinCredenciales() {
        when(bearer.extraerCredencial(any())).thenReturn(Optional.empty());
        when(sesion.extraerCredencial(any())).thenReturn(Optional.empty());
    }

    @Test
    void debeElegirBearer_cuandoHayBearerYCookieSinEncabezado() {
        // Arrange
        conBearer();
        conCookie();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(bearer, TOKEN, SeleccionProveedor.SIN_EMISOR));
    }

    @Test
    void debeElegirSesion_cuandoSoloHayCookie() {
        // Arrange
        when(bearer.extraerCredencial(any())).thenReturn(Optional.empty());
        conCookie();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(sesion, COOKIE, SeleccionProveedor.SIN_EMISOR));
    }

    @Test
    void debeNoActuar_cuandoNingunProveedorTraeCredencial() {
        // Arrange
        sinCredenciales();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isInstanceOf(SeleccionProveedor.Seleccion.NoActua.class);
    }

    @Test
    void debeElegirBearer_cuandoElEncabezadoDiceBearer() {
        // Arrange
        conBearer();
        conCookie();
        request.addHeader(SeleccionProveedor.CABECERA, "BEARER");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(bearer, TOKEN, SeleccionProveedor.SIN_EMISOR));
    }

    @Test
    void debeElegirSesion_cuandoCabeceraIdAunqueHayaBearer() {
        // Arrange
        conBearer();
        conCookie();
        request.addHeader(SeleccionProveedor.CABECERA, " sesion-pdp ");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(sesion, COOKIE, SeleccionProveedor.SIN_EMISOR));
    }

    @Test
    void debeIgnorarLaCookie_cuandoLaRutaEsNoDeNegocioYNoHayEncabezado() {
        // Arrange
        conCookie();
        when(bearer.extraerCredencial(any())).thenReturn(Optional.empty());

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, true);

        // Assert
        assertThat(seleccion).isInstanceOf(SeleccionProveedor.Seleccion.NoActua.class);
    }

    @Test
    void debeRechazar_cuandoElEncabezadoNombraUnProveedorNoAplicableEnRutaNoDeNegocio() {
        // Arrange
        conCookie();
        request.addHeader(SeleccionProveedor.CABECERA, "sesion-pdp");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, true);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_NO_APLICABLE));
    }

    @Test
    void debeRechazar_cuandoValorDesconocidoODeshabilitado() {
        // Arrange
        var desconocido = new MockHttpServletRequest();
        desconocido.addHeader(SeleccionProveedor.CABECERA, "principal");
        var none = new MockHttpServletRequest();
        none.addHeader(SeleccionProveedor.CABECERA, "none");
        var deshabilitado = new MockHttpServletRequest();
        deshabilitado.addHeader(SeleccionProveedor.CABECERA, "apagado");

        // Act
        var seleccionDesconocido = SeleccionProveedor.resolver(desconocido, registro, false);
        var seleccionNone = SeleccionProveedor.resolver(none, registro, true);
        var seleccionDeshabilitado = SeleccionProveedor.resolver(deshabilitado, registro, false);

        // Assert
        assertThat(seleccionDesconocido)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_DESCONOCIDO));
        assertThat(seleccionNone)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_DESCONOCIDO));
        assertThat(seleccionDeshabilitado)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_DESHABILITADO));
    }

    @Test
    void debeRechazar_cuandoProveedorForzadoSinCredencial() {
        // Arrange
        sinCredenciales();
        request.addHeader(SeleccionProveedor.CABECERA, "sesion-pdp");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_SIN_CREDENCIAL));
    }

    @Test
    void debeElegirBearerConEmisor_cuandoElEncabezadoEsUnEmisorHabilitado() {
        // Arrange
        conBearer();
        request.addHeader(SeleccionProveedor.CABECERA, "legado");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(bearer, TOKEN, "legado"));
    }

    @Test
    void debeUsarElIdCanonicoDelEmisor_cuandoElEncabezadoViajaConOtrasMayusculas() {
        // Arrange
        conBearer();
        request.addHeader(SeleccionProveedor.CABECERA, " LEGADO ");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(bearer, TOKEN, "legado"));
    }

    @Test
    void debeRechazarDeshabilitado_cuandoElEmisorEstaDeclaradoPeroApagado() {
        // Arrange
        conBearer();
        request.addHeader(SeleccionProveedor.CABECERA, "inactivo");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_DESHABILITADO));
    }

    @Test
    void debeRechazarDesconocido_cuandoNingunEmisorNiCanalCoincide() {
        // Arrange
        conBearer();
        request.addHeader(SeleccionProveedor.CABECERA, "inexistente");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_DESCONOCIDO));
    }

    @Test
    void debeRechazarSinCredencial_cuandoElEmisorSeEligeSinBearer() {
        // Arrange
        sinCredenciales();
        request.addHeader(SeleccionProveedor.CABECERA, "legado");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, false);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_SIN_CREDENCIAL));
    }

    @Test
    void debeRechazarNoAplicable_cuandoLaRutaEsNoDeNegocioYElProveedorNoActua() {
        // Arrange
        when(bearer.actuaEnRutasNoDeNegocio()).thenReturn(false);
        conBearer();
        request.addHeader(SeleccionProveedor.CABECERA, "legado");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, registro, true);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_NO_APLICABLE));
    }
}
