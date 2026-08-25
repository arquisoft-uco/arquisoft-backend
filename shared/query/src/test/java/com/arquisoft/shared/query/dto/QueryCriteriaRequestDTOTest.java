package com.arquisoft.shared.query.dto;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryCriteriaRequestDTOTest {

    @Test
    void debeDevolverLosValoresPorDefecto_cuandoLaSolicitudEsNula() {
        QueryCriteriaRequestDTO solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(null);

        assertThat(solicitud).isNotNull();
        assertThat(solicitud.getPagina()).isZero();
        assertThat(solicitud.getTamanio()).isEqualTo(10);
        assertThat(solicitud.parsearOrdenamiento()).isEmpty();
        assertThat(solicitud.parsearFiltros()).isNull();
    }

    @Test
    void debeConservarLaSolicitud_cuandoNoEsNula() {
        QueryCriteriaRequestDTO original = new QueryCriteriaRequestDTO();
        original.setPagina(3);

        QueryCriteriaRequestDTO resultado = QueryCriteriaRequestDTO.aplicarPorDefecto(original);

        assertThat(resultado).isSameAs(original);
        assertThat(resultado.getPagina()).isEqualTo(3);
    }

    @Test
    void debeDevolverOrdenamientoVacio_cuandoLaListaEsNulaOVacia() {
        QueryCriteriaRequestDTO solicitud = new QueryCriteriaRequestDTO();

        assertThat(solicitud.parsearOrdenamiento()).isEmpty();

        solicitud.setOrdenamiento(List.of());
        assertThat(solicitud.parsearOrdenamiento()).isEmpty();
    }

    @Test
    void debeReportarCampoVacio_cuandoElOrdenamientoTraeUnElementoNulo() {
        // Arrange
        QueryCriteriaRequestDTO solicitud = new QueryCriteriaRequestDTO();
        solicitud.setOrdenamiento(Arrays.asList("titulo:ASC", null));

        // Act & Assert
        assertThatThrownBy(solicitud::parsearOrdenamiento)
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeReportarOperadorInvalido_cuandoElPredicadoNoTraeOperador() {
        // Arrange
        PredicadoFiltroDTO predicado = new PredicadoFiltroDTO();
        predicado.setCampo("tituloProyecto");
        predicado.setValor("Arquisoft");

        QueryCriteriaRequestDTO solicitud = new QueryCriteriaRequestDTO();
        solicitud.setFiltros(predicado);

        // Act & Assert
        assertThatThrownBy(solicitud::parsearFiltros)
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeParsearUnPredicadoMultivalor_cuandoElFiltroTraeVariosValores() {
        PredicadoMultivalorFiltroDTO predicado = new PredicadoMultivalorFiltroDTO();
        predicado.setCampo("asesorId");
        predicado.setOperador("IN");
        predicado.setValores(List.of("a", "b"));

        QueryCriteriaRequestDTO solicitud = new QueryCriteriaRequestDTO();
        solicitud.setFiltros(predicado);

        assertThat(solicitud.parsearFiltros())
                .isInstanceOf(NodoFiltro.PredicadoMultivalor.class)
                .isEqualTo(NodoFiltro.predicadoMultivalor("asesorId", FiltroOperador.IN, List.of("a", "b")));
    }

    @Test
    void debeParsearElOrdenamiento_cuandoLaListaTraeExpresiones() {
        QueryCriteriaRequestDTO solicitud = new QueryCriteriaRequestDTO();
        solicitud.setOrdenamiento(List.of("titulo:DESC"));

        assertThat(solicitud.parsearOrdenamiento())
                .singleElement()
                .satisfies(orden -> {
                    assertThat(orden.getCampo()).isEqualTo("titulo");
                    assertThat(orden.getDireccion().name()).isEqualTo("DESC");
                });
    }
}
