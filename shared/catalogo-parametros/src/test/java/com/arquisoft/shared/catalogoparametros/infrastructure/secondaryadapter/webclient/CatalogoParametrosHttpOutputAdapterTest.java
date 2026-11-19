package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model.ObtencionParametrosCatalogo;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoParametrosHttpOutputAdapterTest {

    private static final String TOKEN_ID = "0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11";
    private static final String SECRETO = "secreto-de-prueba-123";

    @Mock
    private ClienteHttp cliente;

    private CatalogoParametrosHttpOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        adaptador = new CatalogoParametrosHttpOutputAdapter(cliente, propiedades(TOKEN_ID, SECRETO));
    }

    private static CatalogoParametrosProperties propiedades(String tokenId, String secreto) {
        return new CatalogoParametrosProperties(true, "https://catalogo.test", tokenId, secreto,
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
                        new RuntimeException("http://catalogo.test/tokens/" + TOKEN_ID + "/parametros " + SECRETO)));

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.NoDisponible("ClienteHttpTransporteException"));
        assertThat(resultado.toString()).doesNotContain(SECRETO).doesNotContain(TOKEN_ID);
    }

    @Test
    void debeSinConfigurar_sinLlamar() {
        // Arrange
        var sinSecreto = new CatalogoParametrosHttpOutputAdapter(cliente, propiedades(TOKEN_ID, ""));

        // Act
        var resultado = sinSecreto.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionParametrosCatalogo.SinConfigurar());
        verifyNoInteractions(cliente);
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
        assertThat(solicitud.ruta()).isEqualTo("/tokens/" + TOKEN_ID + "/parametros");
        assertThat(solicitud.cabeceras()).containsEntry("Authorization", "Bearer " + SECRETO)
                .containsEntry("Accept", "application/json");
    }
}
