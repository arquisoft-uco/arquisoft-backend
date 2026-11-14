package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.ConfiguracionClienteHttp;
import com.arquisoft.shared.webclient.FabricaClienteHttp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuariosClienteHttpConfigTest {

    @Mock
    private FabricaClienteHttp fabrica;

    @Test
    void debeCrearUnClientePorDestino_conLaBaseDeUsuariosYElOrigenDelToken() {
        // Arrange
        var propiedades = new UsuarioSesionProperties(" https://usuarios.test/ ", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), Duration.ofMinutes(5), "https://kc.test:8443/realms/a/protocol/token",
                "cliente", "secreto", "/api/usuarios/por-correo", "legado");
        when(fabrica.crear(any(ConfiguracionClienteHttp.class))).thenReturn(mock(ClienteHttp.class));
        var config = new UsuariosClienteHttpConfig(propiedades, fabrica);
        var captor = ArgumentCaptor.forClass(ConfiguracionClienteHttp.class);

        // Act
        config.seguridadUsuariosClienteHttp();
        config.seguridadUsuariosTokenClienteHttp();

        // Assert
        verify(fabrica, times(2)).crear(captor.capture());
        assertThat(captor.getAllValues()).extracting(ConfiguracionClienteHttp::urlBase)
                .containsExactly("https://usuarios.test", "https://kc.test:8443");
        assertThat(captor.getAllValues()).allSatisfy(configuracion -> {
            assertThat(configuracion.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
            assertThat(configuracion.readTimeout()).isEqualTo(Duration.ofSeconds(3));
        });
    }

    @Test
    void debeSepararOrigenYRuta_cuandoLaUrlEsValida() {
        // Act
        var origen = UsuariosClienteHttpConfig.origen(" https://kc.test:8443/realms/a/token ");
        var ruta = UsuariosClienteHttpConfig.ruta("https://kc.test:8443/realms/a/token");
        var rutaVacia = UsuariosClienteHttpConfig.ruta("https://kc.test");

        // Assert
        assertThat(origen).isEqualTo("https://kc.test:8443");
        assertThat(ruta).isEqualTo("/realms/a/token");
        assertThat(rutaVacia).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoLaUrlNoTieneEsquemaONoEsUnaUri() {
        // Act / Assert
        assertThat(UsuariosClienteHttpConfig.origen("kc.test/realms")).isEmpty();
        assertThat(UsuariosClienteHttpConfig.origen("https://kc test")).isEmpty();
        assertThat(UsuariosClienteHttpConfig.ruta("https://kc test")).isEmpty();
    }
}
