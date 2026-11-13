package com.arquisoft.biblioteca.application.documentacion.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentacionCriteriaTest {

    @Test
    void debeAceptarFiltrosYOrdenPermitidos_cuandoSeConstruye() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoDocumentacion", FiltroOperador.ES, "PUBLICADO");
        var orden = List.of(SortOrder.of("fechaPublicacion", SortDirection.DESC));

        // Act
        var criteria = DocumentacionCriteria.builder().pagina(1).tamanio(5).ordenamiento(orden).raiz(raiz).build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(DocumentacionCriteria.Campo.esValidoParaFiltrar("entregableProyectoGrado")).isTrue();
        assertThat(DocumentacionCriteria.Campo.esValidoParaFiltrar("bibliotecario")).isTrue();
        assertThat(DocumentacionCriteria.Campo.esValidoParaFiltrar("fechaPublicacion")).isFalse();
        assertThat(DocumentacionCriteria.Campo.esValidoParaOrdenar("estadoDocumentacion")).isFalse();
    }

    @Test
    void debeRechazarFiltroFueraDeLaListaBlanca_cuandoFiltraPorTituloOAutor() {
        // Arrange
        var porTitulo = NodoFiltro.predicado("titulo", FiltroOperador.ES, "x");
        var porAutor = NodoFiltro.predicado("autor", FiltroOperador.ES, "x");
        var builder = DocumentacionCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(porTitulo)).isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> builder.raiz(porAutor)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeRechazarOrdenPorCampoNoOrdenable_cuandoOrdenaPorEstado() {
        // Arrange
        var orden = List.of(SortOrder.of("estadoDocumentacion", SortDirection.ASC));
        var builder = DocumentacionCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }
}
