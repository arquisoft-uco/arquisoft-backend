package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryCriteriaRequestDTODeserializacionTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void debeConservarTodosLosCampos_cuandoElCuerpoEstaCompleto() {
        // Arrange
        var cuerpo = """
                {
                  "pagina": 2,
                  "tamanio": 5,
                  "ordenamiento": ["tituloProyecto:DESC"],
                  "filtros": {"tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE", "valor": "web"}
                }
                """;

        // Act
        var solicitud = mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class);

        // Assert
        assertThat(solicitud.getPagina()).isEqualTo(2);
        assertThat(solicitud.getTamanio()).isEqualTo(5);
        assertThat(solicitud.getOrdenamiento()).containsExactly("tituloProyecto:DESC");
        assertThat(solicitud.parsearFiltros())
                .isEqualTo(NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web"));
    }

    @Test
    void debeAplicarLosValoresPorDefecto_cuandoElCuerpoEstaVacio() {
        // Arrange
        var cuerpo = "{}";

        // Act
        var solicitud = mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class);

        // Assert
        assertThat(solicitud.getPagina()).isZero();
        assertThat(solicitud.getTamanio()).isEqualTo(10);
        assertThat(solicitud.getOrdenamiento()).isNull();
        assertThat(solicitud.getFiltros()).isNull();
    }

    @Test
    void debeAplicarLaPaginaPorDefecto_cuandoSoloLlegaElTamanio() {
        // Arrange
        var cuerpo = "{\"tamanio\": 2}";

        // Act
        var solicitud = mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class);

        // Assert
        assertThat(solicitud.getPagina()).isZero();
        assertThat(solicitud.getTamanio()).isEqualTo(2);
    }

    @Test
    void debeAplicarLaPaginacionPorDefecto_cuandoSoloLleganFiltrosYOrden() {
        // Arrange
        var cuerpo = """
                {
                  "ordenamiento": ["tituloProyecto:ASC"],
                  "filtros": {"tipo": "PREDICADO", "campo": "estadoFicha", "operador": "ES", "valor": "APROBADA"}
                }
                """;

        // Act
        var solicitud = mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class);

        // Assert
        assertThat(solicitud.getPagina()).isZero();
        assertThat(solicitud.getTamanio()).isEqualTo(10);
        assertThat(solicitud.getOrdenamiento()).isEqualTo(List.of("tituloProyecto:ASC"));
        assertThat(solicitud.getFiltros()).isInstanceOf(PredicadoFiltroDTO.class);
    }

    @Test
    void debeIgnorarCamposDesconocidos_cuandoElCuerpoLosTrae() {
        // Arrange
        var cuerpo = "{\"pagina\": 1, \"tamanio\": 3, \"otro\": true}";

        // Act
        var solicitud = mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class);

        // Assert
        assertThat(solicitud.getPagina()).isEqualTo(1);
        assertThat(solicitud.getTamanio()).isEqualTo(3);
    }

    @Test
    void debeRechazarElCuerpo_cuandoLaPaginaLlegaNulaExplicitamente() {
        // Arrange
        var cuerpo = "{\"pagina\": null}";

        // Act & Assert
        assertThatThrownBy(() -> mapper.readValue(cuerpo, QueryCriteriaRequestDTO.class))
                .isInstanceOf(MismatchedInputException.class);
    }
}
