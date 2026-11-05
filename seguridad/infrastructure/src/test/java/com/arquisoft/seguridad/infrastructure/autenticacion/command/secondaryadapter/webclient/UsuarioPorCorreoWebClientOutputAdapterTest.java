package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.UsuarioPorCorreoRequestDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.dto.UsuarioPorCorreoResponseDTO;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.UsuariosNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPorCorreoWebClientOutputAdapterTest {

    private static final String CORREO = "ana@uco.edu.co";

    @Mock
    private ClienteHttp clienteUsuarios;

    @Mock
    private CredencialServicioUsuarios credencial;

    @Mock
    private AppLogger logger;

    private UsuarioPorCorreoWebClientOutputAdapter adaptador() {
        var propiedades = new UsuarioSesionProperties("https://usuarios.test", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), Duration.ofMinutes(5), "https://kc.test/token", "cliente", "secreto",
                "/api/usuarios/por-correo");
        return new UsuarioPorCorreoWebClientOutputAdapter(clienteUsuarios, credencial, propiedades, logger);
    }

    private void conRespuesta(int estado, UsuarioPorCorreoResponseDTO cuerpo) {
        when(clienteUsuarios.ejecutar(any(SolicitudHttp.class), eq(UsuarioPorCorreoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(estado, cuerpo));
    }

    @Test
    void debeDevolverElId_cuando200() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(credencial.token()).thenReturn("tok");
        conRespuesta(200, new UsuarioPorCorreoResponseDTO(usuario.toString()));

        // Act
        var id = adaptador().obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
    }

    @Test
    void debeDevolverVacio_cuando422() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        conRespuesta(422, null);

        // Act
        var id = adaptador().obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).isEmpty();
    }

    @Test
    void debeDevolverVacioSinLlamar_cuandoElCorreoEstaVacio() {
        // Act
        var id = adaptador().obtenerIdPorCorreo("  ");

        // Assert
        assertThat(id).isEmpty();
        verify(clienteUsuarios, never()).ejecutar(any(), any());
    }

    @Test
    void debeLanzarNoDisponible_cuando400401403() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        var adaptador = adaptador();

        for (var estado : new int[]{400, 401, 403}) {
            conRespuesta(estado, null);

            // Act / Assert
            assertThatThrownBy(() -> adaptador.obtenerIdPorCorreo(CORREO))
                    .isInstanceOf(UsuariosNoDisponibleException.class);
        }
    }

    @Test
    void debeLanzarNoDisponible_cuando5xx() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        conRespuesta(503, null);
        var adaptador = adaptador();

        // Act / Assert
        assertThatThrownBy(() -> adaptador.obtenerIdPorCorreo(CORREO))
                .isInstanceOf(UsuariosNoDisponibleException.class)
                .isInstanceOf(InfrastructureException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuando200ConCuerpoInvalido() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        conRespuesta(200, new UsuarioPorCorreoResponseDTO("no-es-uuid"));
        var adaptador = adaptador();

        // Act / Assert
        assertThatThrownBy(() -> adaptador.obtenerIdPorCorreo(CORREO))
                .isInstanceOf(UsuariosNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoHayFalloDeTransporte() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        when(clienteUsuarios.ejecutar(any(SolicitudHttp.class), eq(UsuarioPorCorreoResponseDTO.class)))
                .thenThrow(new ClienteHttpTransporteException("timeout", null));
        var adaptador = adaptador();

        // Act / Assert
        assertThatThrownBy(() -> adaptador.obtenerIdPorCorreo(CORREO))
                .isInstanceOf(UsuariosNoDisponibleException.class);
    }

    @Test
    void debeAdjuntarElBearerDeServicio() {
        // Arrange
        when(credencial.token()).thenReturn("tok-servicio");
        conRespuesta(422, null);
        var captor = ArgumentCaptor.forClass(SolicitudHttp.class);

        // Act
        adaptador().obtenerIdPorCorreo(CORREO);

        // Assert
        verify(clienteUsuarios).ejecutar(captor.capture(), eq(UsuarioPorCorreoResponseDTO.class));
        assertThat(captor.getValue().cabeceras()).containsEntry("Authorization", "Bearer tok-servicio");
        assertThat(captor.getValue().ruta()).isEqualTo("/api/usuarios/por-correo");
        assertThat(captor.getValue().cuerpo()).isEqualTo(new UsuarioPorCorreoRequestDTO(CORREO));
    }

    @Test
    void debeNoAdjuntarLaCookieDelLlamante() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        conRespuesta(422, null);
        var captor = ArgumentCaptor.forClass(SolicitudHttp.class);

        // Act
        adaptador().obtenerIdPorCorreo(CORREO);

        // Assert
        verify(clienteUsuarios).ejecutar(captor.capture(), eq(UsuarioPorCorreoResponseDTO.class));
        assertThat(captor.getValue().cabeceras()).doesNotContainKey("Cookie");
        assertThat(captor.getValue().cabeceras().keySet())
                .noneMatch(cabecera -> cabecera.equalsIgnoreCase("cookie"));
    }

    @Test
    void debeInvalidarElTokenYReintentarUnaVez_cuando401() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(credencial.token()).thenReturn("viejo", "nuevo");
        when(clienteUsuarios.ejecutar(any(SolicitudHttp.class), eq(UsuarioPorCorreoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(401, null))
                .thenReturn(new RespuestaHttp<>(200, new UsuarioPorCorreoResponseDTO(usuario.toString())));

        // Act
        var id = adaptador().obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
        verify(credencial).invalidar();
        verify(clienteUsuarios, times(2)).ejecutar(any(SolicitudHttp.class), eq(UsuarioPorCorreoResponseDTO.class));
    }

    @Test
    void debeLanzarNoDisponible_cuando200SinCuerpo() {
        // Arrange
        when(credencial.token()).thenReturn("tok");
        conRespuesta(200, null);
        var adaptador = adaptador();

        // Act / Assert
        assertThatThrownBy(() -> adaptador.obtenerIdPorCorreo(CORREO))
                .isInstanceOf(UsuariosNoDisponibleException.class);
    }
}
