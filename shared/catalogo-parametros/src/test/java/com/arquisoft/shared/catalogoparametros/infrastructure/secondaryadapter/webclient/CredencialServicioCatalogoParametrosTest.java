package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient;

import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.TokenServicioCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.model.ObtencionTokenServicio;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredencialServicioCatalogoParametrosTest {

    private static final String CLIENT_ID = "svc-arquisoft-catalogo-dev";
    private static final String SECRETO = "secreto-de-prueba-123";
    private static final String JWT = "jwt-de-servicio";

    @Mock
    private ClienteHttp clienteToken;

    private CredencialServicioCatalogoParametros credencial;

    @BeforeEach
    void preparar() {
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test",
                "https://keycloak.test/realms/r/protocol/openid-connect/token", CLIENT_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());
        credencial = new CredencialServicioCatalogoParametros(clienteToken, propiedades);
    }

    private void responderToken(int estado, TokenServicioCatalogoResponseDTO cuerpo) {
        when(clienteToken.ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(estado, cuerpo));
    }

    @Test
    void debeObtenerToken_conClientCredentials() {
        // Arrange
        responderToken(200, new TokenServicioCatalogoResponseDTO(JWT, 300));
        var captor = ArgumentCaptor.forClass(SolicitudHttp.class);

        // Act
        var resultado = credencial.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionTokenServicio.Vigente(JWT));
        verify(clienteToken).ejecutar(captor.capture(), eq(TokenServicioCatalogoResponseDTO.class));
        var solicitud = captor.getValue();
        assertThat(solicitud.metodo()).isEqualTo(MetodoHttp.POST);
        assertThat(solicitud.ruta()).isEqualTo("/realms/r/protocol/openid-connect/token");
        assertThat(solicitud.tipoContenido()).isEqualTo(TipoContenido.FORM);
        assertThat(solicitud.cuerpo()).isEqualTo(Map.of(
                "grant_type", "client_credentials", "client_id", CLIENT_ID, "client_secret", SECRETO));
    }

    @Test
    void debeReutilizarElToken_mientrasEsteVigente() {
        // Arrange
        responderToken(200, new TokenServicioCatalogoResponseDTO(JWT, 300));

        // Act
        credencial.obtener();
        var segundo = credencial.obtener();

        // Assert
        assertThat(segundo).isEqualTo(new ObtencionTokenServicio.Vigente(JWT));
        verify(clienteToken, times(1)).ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class));
    }

    @Test
    void debeRenovarElToken_cuandoVenceDentroDelMargen() {
        // Arrange
        responderToken(200, new TokenServicioCatalogoResponseDTO(JWT, 10));

        // Act
        credencial.obtener();
        credencial.obtener();

        // Assert
        verify(clienteToken, times(2)).ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class));
    }

    @Test
    void debeRenovarElToken_cuandoSeInvalida() {
        // Arrange
        responderToken(200, new TokenServicioCatalogoResponseDTO(JWT, 300));

        // Act
        credencial.obtener();
        credencial.invalidar();
        credencial.obtener();

        // Assert
        verify(clienteToken, times(2)).ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class));
    }

    @Test
    void debeRechazada_cuandoKeycloakResponde400o401() {
        // Arrange
        when(clienteToken.ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(400, null))
                .thenReturn(new RespuestaHttp<>(401, null));

        // Act / Assert
        assertThat(credencial.obtener()).isEqualTo(new ObtencionTokenServicio.Rechazada(400));
        assertThat(credencial.obtener()).isEqualTo(new ObtencionTokenServicio.Rechazada(401));
    }

    @Test
    void debeNoDisponible_cuandoKeycloakResponde5xx() {
        // Arrange
        responderToken(503, null);

        // Act
        var resultado = credencial.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionTokenServicio.NoDisponible("token-estado-503"));
    }

    @Test
    void debeNoDisponible_cuandoLaRespuestaNoTraeToken() {
        // Arrange
        when(clienteToken.ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, null))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioCatalogoResponseDTO(" ", 300)));

        // Act / Assert
        assertThat(credencial.obtener()).isEqualTo(new ObtencionTokenServicio.NoDisponible("token-servicio-invalido"));
        assertThat(credencial.obtener()).isEqualTo(new ObtencionTokenServicio.NoDisponible("token-servicio-invalido"));
    }

    @Test
    void debeNoDisponible_sinExponerElSecreto_cuandoTransporteCaido() {
        // Arrange
        when(clienteToken.ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class)))
                .thenThrow(new ClienteHttpTransporteException("ResourceAccessException",
                        new RuntimeException("client_secret=" + SECRETO)));

        // Act
        var resultado = credencial.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionTokenServicio.NoDisponible("ClienteHttpTransporteException"));
        assertThat(resultado.toString()).doesNotContain(SECRETO);
    }
}
