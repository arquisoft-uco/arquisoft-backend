package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.ConfiguracionClienteHttp;
import com.arquisoft.shared.webclient.FabricaClienteHttp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoParametrosClienteHttpConfigTest {

    @Mock
    private FabricaClienteHttp fabrica;
    @Mock
    private ClienteHttp cliente;

    @Test
    void debeConstruirElCliente_conLaConfiguracion() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "  https://catalogo.test/api/v1//  ",
                "https://keycloak.test/realms/r/protocol/openid-connect/token", "svc-arquisoft", "secreto",
                Duration.ofSeconds(2), Duration.ofSeconds(7),
                false, Duration.ofMinutes(5), Map.of());
        var captor = ArgumentCaptor.forClass(ConfiguracionClienteHttp.class);
        when(fabrica.crear(captor.capture())).thenReturn(cliente);
        var config = new CatalogoParametrosClienteHttpConfig(propiedades, fabrica);

        // Act
        var resultado = config.catalogoParametrosClienteHttp();

        // Assert
        verify(fabrica).crear(captor.getValue());
        assertThat(resultado).isSameAs(cliente);
        assertThat(captor.getValue().urlBase()).isEqualTo("https://catalogo.test/api/v1");
        assertThat(captor.getValue().connectTimeout()).isEqualTo(Duration.ofSeconds(2));
        assertThat(captor.getValue().readTimeout()).isEqualTo(Duration.ofSeconds(7));
    }

    @Test
    void debeConstruirElClienteDeToken_conElOrigenDelTokenUri() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test/api/v1",
                "https://keycloak.test:8443/realms/r/protocol/openid-connect/token", "svc-arquisoft", "secreto",
                Duration.ofSeconds(2), Duration.ofSeconds(7), false, Duration.ofMinutes(5), Map.of());
        var captor = ArgumentCaptor.forClass(ConfiguracionClienteHttp.class);
        when(fabrica.crear(captor.capture())).thenReturn(cliente);
        var config = new CatalogoParametrosClienteHttpConfig(propiedades, fabrica);

        // Act
        var resultado = config.catalogoParametrosTokenClienteHttp();

        // Assert
        assertThat(resultado).isSameAs(cliente);
        assertThat(captor.getValue().urlBase()).isEqualTo("https://keycloak.test:8443");
        assertThat(captor.getValue().connectTimeout()).isEqualTo(Duration.ofSeconds(2));
        assertThat(captor.getValue().readTimeout()).isEqualTo(Duration.ofSeconds(7));
    }
}
