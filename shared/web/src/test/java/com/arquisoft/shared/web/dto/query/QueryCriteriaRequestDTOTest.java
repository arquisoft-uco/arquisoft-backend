package com.arquisoft.shared.web.dto.query;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryCriteriaRequestDTOTest {

    // Los endpoints de consulta declaran @RequestBody(required = false), asi que el mapper recibe
    // null cuando el cliente no manda cuerpo. La politica de «sin cuerpo = primera pagina con el
    // tamanio por defecto» se decide aqui para que ningun RequestMapper la repita.
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
