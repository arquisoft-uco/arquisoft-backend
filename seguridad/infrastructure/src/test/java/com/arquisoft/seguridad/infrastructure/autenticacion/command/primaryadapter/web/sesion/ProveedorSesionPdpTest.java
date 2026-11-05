package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.sesion;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.ResolverIdentidadSesionInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorSesionPdpTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=abc";

    @Mock
    private ResolverIdentidadSesionInteractor interactor;

    private ProveedorSesionPdp proveedor() {
        var propiedades = new SesionProperties("sesion-pdp", true, "SECURITY_BASELINE_SESSION",
                "https://pdp.test", false, Duration.ofSeconds(1), Duration.ofSeconds(3),
                new SesionProperties.Cache(Duration.ofSeconds(60), 100));
        return new ProveedorSesionPdp(propiedades, interactor);
    }

    @Test
    void debeExtraerSoloLaCookieDeSesion_cuandoLlegaConOtrasCookies() {
        // Arrange
        var request = new MockHttpServletRequest();
        request.setCookies(new Cookie("otra", "x"), new Cookie("SECURITY_BASELINE_SESSION", "abc"),
                new Cookie("XSRF-TOKEN", "y"));
        var sinSesion = new MockHttpServletRequest();
        sinSesion.setCookies(new Cookie("otra", "x"), new Cookie("SECURITY_BASELINE_SESSION", ""));

        // Act
        var credencial = proveedor().extraerCredencial(request);

        // Assert
        assertThat(credencial).contains(COOKIE);
        assertThat(proveedor().extraerCredencial(sinSesion)).isEmpty();
        assertThat(proveedor().extraerCredencial(new MockHttpServletRequest())).isEmpty();
    }

    @Test
    void debeAdjuntarCookie_cuandoSeReenviaAlPep() {
        // Arrange
        var cabeceras = new HttpHeaders();

        // Act
        proveedor().adjuntarCredencial(cabeceras, COOKIE);

        // Assert
        assertThat(cabeceras.getFirst(HttpHeaders.COOKIE)).isEqualTo(COOKIE);
        assertThat(cabeceras.containsHeader(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(proveedor().autorizacion("sesion-pdp")).contains(ModoAutorizacion.PEP);
        assertThat(proveedor().autorizacion("principal")).isEmpty();
        assertThat(proveedor().autenticaPrimero()).isFalse();
        assertThat(proveedor().actuaEnRutasNoDeNegocio()).isFalse();
        assertThat(proveedor().exigeProteccionCsrf()).isTrue();
    }

    @Test
    void debeAutenticarComoIdentidadConContrato_cuandoLaSesionResuelve() {
        // Arrange
        when(interactor.ejecutar(ResolverIdentidadSesionCommand.crear(COOKIE)))
                .thenReturn(new ResolucionSesionResult.Resuelta("sub-1", "Ana", "ana@uco.edu.co", "t-1"));

        // Act
        var autenticacion = proveedor().autenticar(COOKIE);

        // Assert
        assertThat(autenticacion.isAuthenticated()).isTrue();
        assertThat(autenticacion).isInstanceOfSatisfying(SesionAuthenticationToken.class, token -> {
            assertThat(token.proveedor()).isEqualTo("sesion-pdp");
            assertThat(token.emisor()).isEqualTo("sesion-pdp");
            assertThat(token.sujeto()).isEqualTo("sub-1");
            assertThat(token.nombreUsuario()).isEqualTo("Ana");
            assertThat(token.correo()).isEqualTo("ana@uco.edu.co");
            assertThat(token.getTenant()).isEqualTo("t-1");
            assertThat(token.getName()).isEqualTo("sub-1");
            assertThat(token.roles()).isEmpty();
            assertThat(token.idToken()).isNull();
            assertThat(token.expiraEn()).isNull();
            assertThat(token.getAuthorities()).isEmpty();
            assertThat(token.getCredentials()).isNull();
        });
    }

    @Test
    void debeUsarElCorreoComoNombreUsuario_cuandoLaSesionNoTraeNombre() {
        // Arrange
        var token = new SesionAuthenticationToken("sesion-pdp", "sub-1", "", "ana@uco.edu.co", "t-1");

        // Act / Assert
        assertThat(token.nombreUsuario()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeLanzar401_cuandoSinSesion() {
        // Arrange
        when(interactor.ejecutar(ResolverIdentidadSesionCommand.crear(COOKIE)))
                .thenReturn(new ResolucionSesionResult.SinSesion());
        var proveedor = proveedor();

        // Act / Assert
        assertThatThrownBy(() -> proveedor.autenticar(COOKIE)).isInstanceOf(InsufficientAuthenticationException.class);
        verify(interactor).ejecutar(ResolverIdentidadSesionCommand.crear(COOKIE));
    }

    @Test
    void debeLanzarAccesoDenegado_cuandoUsuarioNoRegistrado() {
        // Arrange
        when(interactor.ejecutar(ResolverIdentidadSesionCommand.crear(COOKIE)))
                .thenReturn(new ResolucionSesionResult.UsuarioNoRegistrado());
        var proveedor = proveedor();

        // Act / Assert
        assertThatThrownBy(() -> proveedor.autenticar(COOKIE)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void debeExponerIdYHabilitacion_segunLasPropiedades() {
        // Arrange
        var apagado = new ProveedorSesionPdp(new SesionProperties("otra-sesion", false, "COOKIE",
                "https://pdp.test", false, Duration.ofSeconds(1), Duration.ofSeconds(3),
                new SesionProperties.Cache(Duration.ofSeconds(60), 100)), interactor);

        // Act / Assert
        assertThat(proveedor().id()).isEqualTo("sesion-pdp");
        assertThat(proveedor().habilitado()).isTrue();
        assertThat(apagado.id()).isEqualTo("otra-sesion");
        assertThat(apagado.habilitado()).isFalse();
        assertThat(apagado.autorizacion("sesion-pdp")).isEmpty();
    }
}
