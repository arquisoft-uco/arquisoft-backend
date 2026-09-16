package com.arquisoft.solicitudes.application.respuesta.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RespuestaCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("contenido")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("contenido")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("fechaRespuesta")).isFalse();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("fechaRespuesta")).isTrue();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("estadoRespuestaId")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("estadoRespuestaId")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("tipoSolicitudId")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("tipoSolicitudId")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("remitenteUsuarioId")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("remitenteUsuarioId")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("destinatarioUsuarioId")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("destinatarioUsuarioId")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("destinatarioIdentificador")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("destinatarioIdentificador")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("destinatarioNombre")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("destinatarioNombre")).isTrue();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("destinatarioEmail")).isTrue();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("destinatarioEmail")).isFalse();

        assertThat(RespuestaCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(RespuestaCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeConstruirCriteria_cuandoRaizFiltraPorDestinatarioUsuarioId() {
        // Arrange
        var raiz = NodoFiltro.predicado("destinatarioUsuarioId", FiltroOperador.ES,
                "3f3e6b0a-6b6d-4f8b-9a8e-8c9d0e1f2a3b");

        // Act
        var criteria = RespuestaCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(raiz)
                .build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");
        var builder = RespuestaCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorDestinatarioUsuarioId() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("destinatarioUsuarioId", SortDirection.ASC));
        var builder = RespuestaCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
