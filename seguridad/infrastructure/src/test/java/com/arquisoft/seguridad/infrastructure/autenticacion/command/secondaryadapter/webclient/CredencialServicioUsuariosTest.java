package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.TokenServicioResponseDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.UsuariosNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredencialServicioUsuariosTest {

    @Mock
    private ClienteHttp clienteToken;

    @Mock
    private AppLogger logger;

    private CredencialServicioUsuarios credencial() {
        var propiedades = new UsuarioSesionProperties("https://usuarios.test", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), Duration.ofMinutes(5), "https://kc.test/realms/arquisoft/protocol/openid-connect/token",
                "arquisoft-seguridad-usuarios", "secreto");
        return new CredencialServicioUsuarios(clienteToken, propiedades, logger);
    }

    @Test
    void debeReutilizarElToken_mientrasNoExpire() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-1", 300)));
        var credencial = credencial();

        // Act
        var primero = credencial.token();
        var segundo = credencial.token();

        // Assert
        assertThat(primero).isEqualTo("tok-1");
        assertThat(segundo).isEqualTo("tok-1");
        verify(clienteToken, times(1)).ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class));
    }

    @Test
    void debeRenovar_cuandoExpira() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-1", 10)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-2", 300)));
        var credencial = credencial();

        // Act
        var primero = credencial.token();
        var segundo = credencial.token();

        // Assert
        assertThat(primero).isEqualTo("tok-1");
        assertThat(segundo).isEqualTo("tok-2");
    }

    @Test
    void debeRenovar_cuandoSeInvalida() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-1", 300)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-2", 300)));
        var credencial = credencial();
        credencial.token();

        // Act
        credencial.invalidar();

        // Assert
        assertThat(credencial.token()).isEqualTo("tok-2");
    }

    @Test
    void debeEnviarClientCredentialsPorFormulario() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, new TokenServicioResponseDTO("tok-1", 300)));
        var captor = ArgumentCaptor.forClass(SolicitudHttp.class);

        // Act
        credencial().token();

        // Assert
        verify(clienteToken).ejecutar(captor.capture(), eq(TokenServicioResponseDTO.class));
        assertThat(captor.getValue().ruta()).isEqualTo("/realms/arquisoft/protocol/openid-connect/token");
        assertThat(captor.getValue().cuerpo().toString())
                .contains("grant_type=client_credentials", "client_id=arquisoft-seguridad-usuarios");
    }

    @Test
    void debeLanzarNoDisponible_cuandoElProveedorRechaza() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(401, null));
        var credencial = credencial();

        // Act / Assert
        assertThatThrownBy(credencial::token).isInstanceOf(UsuariosNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoHayFalloDeTransporte() {
        // Arrange
        when(clienteToken.ejecutar(any(SolicitudHttp.class), eq(TokenServicioResponseDTO.class)))
                .thenThrow(new ClienteHttpTransporteException("timeout", null));
        var credencial = credencial();

        // Act / Assert
        assertThatThrownBy(credencial::token).isInstanceOf(UsuariosNoDisponibleException.class);
    }
}
