package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model.ObtencionParametrosCatalogo;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.model.ObtencionTokenServicio;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoParametrosHttpOutputAdapterTest {

    private static final String TOKEN_ID = "svc-arquisoft-catalogo-dev";
    private static final String SECRETO = "secreto-de-prueba-123";
    private static final String JWT = "jwt-de-servicio";

    @Mock
    private ClienteHttp cliente;

    @Mock
    private CredencialServicioCatalogoParametros credencial;

    private CatalogoParametrosHttpOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        lenient().when(credencial.obtener()).thenReturn(new ObtencionTokenServicio.Vigente(JWT));
        adaptador = new CatalogoParametrosHttpOutputAdapter(cliente, propiedades(TOKEN_ID, SECRETO), credencial);
    }

    private static CatalogoParametrosProperties propiedades(String clientId, String secreto) {
        return new CatalogoParametrosProperties(true, "https://catalogo.test",
                "https://keycloak.test/realms/r/protocol/openid-connect/token", clientId, secreto,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());
    }

    private static ParametrosCatalogoResponseDTO cuerpo(String nombre, Boolean activo, Object... valores) {
        var metadatos = new java.util.ArrayList<ParametrosCatalogoResponseDTO.MetadatoItem>();
        for (var valor : valores) {
            metadatos.add(new ParametrosCatalogoResponseDTO.MetadatoItem(new ParametrosCatalogoResponseDTO.Metadato(valor)));
        }
        return new ParametrosCatalogoResponseDTO(List.of(new ParametrosCatalogoResponseDTO.ParametroItem(
                new ParametrosCatalogoResponseDTO.Parametro(nombre, activo), metadatos)));
    }

    @Test
    void debeObtener_cuando200ConValor() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, cuerpo("PARAM_X", true, "valor")));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ObtencionParametrosCatalogo.Obtenida.class,
                o -> assertThat(o.entity().valores()).containsExactly(Map.entry("PARAM_X", "valor")));
    }

    @Test
    void debeExcluirParametro_cuandoInactivo() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, cuerpo("PARAM_X", false, "valor")));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ObtencionParametrosCatalogo.Obtenida.class,
                o -> assertThat(o.entity().valores()).isEmpty());
    }

    @Test
    void debeTomarElPrimerValorNoVacio_cuandoVariosMetadatos() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, cuerpo("PARAM_X", true, "  ", "segundo", "tercero")));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ObtencionParametrosCatalogo.Obtenida.class,
                o -> assertThat(o.entity().valores()).containsEntry("PARAM_X", "segundo"));
    }

    @Test
    void debeConvertirATexto_cuandoValorEsNumeroOBooleano() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, cuerpo("PARAM_X", true, 42)));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ObtencionParametrosCatalogo.Obtenida.class,
                o -> assertThat(o.entity().valores()).containsEntry("PARAM_X", "42"));
    }

    @Test
    void debeExcluirParametro_cuandoValorEsCompuesto() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, cuerpo("PARAM_X", true, Map.of("a", "b"))));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ObtencionParametrosCatalogo.Obtenida.class,
                o -> assertThat(o.entity().valores()).isEmpty());
    }

    @Test
    void debeNoDisponible_cuandoCuerpoNulo() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, null));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.NoDisponible("respuesta-invalida"));
    }

    @Test
    void debeRechazada_cuando401o403() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(401, null))
                .thenReturn(new RespuestaHttp<>(403, null));

        // Act / Assert
        assertThat(adaptador.obtener()).isEqualTo(new ObtencionParametrosCatalogo.Rechazada(401));
        assertThat(adaptador.obtener()).isEqualTo(new ObtencionParametrosCatalogo.Rechazada(403));
        verify(credencial).invalidar();
    }

    @Test
    void debeNoInvalidarElToken_cuando403() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(403, null));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.Rechazada(403));
        verify(credencial, never()).invalidar();
    }

    @Test
    void debeRechazada_sinLlamarAlCatalogo_cuandoKeycloakRechazaLaCredencial() {
        // Arrange
        when(credencial.obtener()).thenReturn(new ObtencionTokenServicio.Rechazada(401));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.Rechazada(401));
        verifyNoInteractions(cliente);
    }

    @Test
    void debeNoDisponible_sinLlamarAlCatalogo_cuandoKeycloakNoResponde() {
        // Arrange
        when(credencial.obtener()).thenReturn(new ObtencionTokenServicio.NoDisponible("ClienteHttpTransporteException"));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.NoDisponible("ClienteHttpTransporteException"));
        verifyNoInteractions(cliente);
    }

    @Test
    void debeNoDisponible_cuando5xx() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(503, null));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.NoDisponible("estado-503"));
    }

    @Test
    void debeNoDisponible_cuandoTransporteCaido() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenThrow(new ClienteHttpTransporteException("ResourceAccessException",
                        new RuntimeException("http://catalogo.test/parametros/consulta/m2m " + JWT)));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.NoDisponible("ClienteHttpTransporteException"));
        assertThat(resultado.toString()).doesNotContain(JWT);
    }

    @Test
    void debeSinConfigurar_sinLlamar() {
        // Arrange
        var sinSecreto = new CatalogoParametrosHttpOutputAdapter(cliente, propiedades(TOKEN_ID, ""), credencial);

        // Act
        var resultado = sinSecreto.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.SinConfigurar());
        verifyNoInteractions(cliente, credencial);
    }

    @Test
    void debeEnviarGetConBearerYAccept() {
        // Arrange
        when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                .thenReturn(new RespuestaHttp<>(200, new ParametrosCatalogoResponseDTO(List.of())));
        var captor = ArgumentCaptor.forClass(SolicitudHttp.class);

        // Act
        adaptador.obtener();

        // Assert
        verify(cliente).ejecutar(captor.capture(), eq(ParametrosCatalogoResponseDTO.class));
        var solicitud = captor.getValue();
        assertThat(solicitud.metodo()).isEqualTo(MetodoHttp.GET);
        assertThat(solicitud.ruta()).isEqualTo("/parametros/consulta/m2m");
        assertThat(solicitud.cabeceras()).containsEntry("Authorization", "Bearer " + JWT)
                .containsEntry("Accept", "application/json");
    }
}
