package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import jakarta.servlet.http.Cookie;
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

    private ProveedorAutenticacion sesion;
    private ProveedorAutenticacion apagado;
    private ProveedoresAutenticacion registro;
    private MockHttpServletRequest request;

    @BeforeEach
    void preparar() {
        sesion = mock(ProveedorAutenticacion.class);
        when(sesion.id()).thenReturn("sesion-pdp");
        when(sesion.habilitado()).thenReturn(true);
        apagado = mock(ProveedorAutenticacion.class);
        when(apagado.id()).thenReturn("apagado");
        when(apagado.habilitado()).thenReturn(false);
        registro = new ProveedoresAutenticacion(List.of(sesion, apagado));
        request = new MockHttpServletRequest("POST", "/api/fichas-perfil");
    }

    private void conCookie() {
        request.setCookies(new Cookie("SECURITY_BASELINE_SESSION", "abc"));
        when(sesion.extraerCredencial(any())).thenReturn(Optional.of(COOKIE));
    }

    private void sinCookie() {
        when(sesion.extraerCredencial(any())).thenReturn(Optional.empty());
    }

    @Test
    void debeNoActuar_cuandoHayBearerSinCabecera() {
        // Arrange
        conCookie();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, true, registro);

        // Assert
        assertThat(seleccion).isInstanceOf(SeleccionProveedor.Seleccion.NoActua.class);
    }

    @Test
    void debeElegirSesion_cuandoSoloHayCookie() {
        // Arrange
        conCookie();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, false, registro);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(sesion, COOKIE));
    }

    @Test
    void debeNoActuar_cuandoNoHayCredenciales() {
        // Arrange
        sinCookie();

        // Act
        var seleccion = SeleccionProveedor.resolver(request, false, registro);

        // Assert
        assertThat(seleccion).isInstanceOf(SeleccionProveedor.Seleccion.NoActua.class);
    }

    @Test
    void debeNoActuar_cuandoCabeceraBearerAunqueHayaCookie() {
        // Arrange
        conCookie();
        request.addHeader(SeleccionProveedor.CABECERA, "BEARER");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, false, registro);

        // Assert
        assertThat(seleccion).isInstanceOf(SeleccionProveedor.Seleccion.NoActua.class);
    }

    @Test
    void debeElegirSesion_cuandoCabeceraIdAunqueHayaBearer() {
        // Arrange
        conCookie();
        request.addHeader(SeleccionProveedor.CABECERA, " sesion-pdp ");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, true, registro);

        // Assert
        assertThat(seleccion).isEqualTo(new SeleccionProveedor.Seleccion.Elegida(sesion, COOKIE));
    }

    @Test
    void debeRechazar_cuandoValorDesconocidoDeshabilitadoONone() {
        // Arrange
        conCookie();
        var desconocido = new MockHttpServletRequest();
        desconocido.addHeader(SeleccionProveedor.CABECERA, "principal");
        var none = new MockHttpServletRequest();
        none.addHeader(SeleccionProveedor.CABECERA, "none");
        var deshabilitado = new MockHttpServletRequest();
        deshabilitado.addHeader(SeleccionProveedor.CABECERA, "apagado");

        // Act
        var seleccionDesconocido = SeleccionProveedor.resolver(desconocido, false, registro);
        var seleccionNone = SeleccionProveedor.resolver(none, true, registro);
        var seleccionDeshabilitado = SeleccionProveedor.resolver(deshabilitado, false, registro);

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
        sinCookie();
        request.addHeader(SeleccionProveedor.CABECERA, "sesion-pdp");

        // Act
        var seleccion = SeleccionProveedor.resolver(request, true, registro);

        // Assert
        assertThat(seleccion)
                .isEqualTo(new SeleccionProveedor.Seleccion.Rechazada(SeleccionProveedor.MOTIVO_SIN_CREDENCIAL));
    }
}
