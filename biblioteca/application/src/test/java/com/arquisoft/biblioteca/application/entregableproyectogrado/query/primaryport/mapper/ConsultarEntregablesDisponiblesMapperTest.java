package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarEntregablesDisponiblesMapperTest {

    @Test
    void debeFijarElOrdenTituloAusenteTituloYVersion_cuandoSeConvierte() {
        // Arrange
        var query = ConsultarEntregablesDisponiblesQuery.crear(0, 10, null);

        // Act
        var criteria = ConsultarEntregablesDisponiblesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(o -> o.getCampo(), o -> o.getDireccion())
                .containsExactly(
                        tuple("tituloAusente", SortDirection.ASC),
                        tuple("titulo", SortDirection.ASC),
                        tuple("versionEntregable", SortDirection.ASC));
    }

    @Test
    void debeOmitirElFiltroYCopiarLaPagina_cuandoElTituloEsVacio() {
        // Arrange
        var query = ConsultarEntregablesDisponiblesQuery.crear(3, 25, "   ");

        // Act
        var criteria = ConsultarEntregablesDisponiblesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isNull();
        assertThat(criteria.getPagina()).isEqualTo(3);
        assertThat(criteria.getTamanio()).isEqualTo(25);
    }

    @Test
    void debeCrearPredicadoContiene_cuandoHayTitulo() {
        // Arrange
        var query = ConsultarEntregablesDisponiblesQuery.crear(0, 10, " gestión ");

        // Act
        var criteria = ConsultarEntregablesDisponiblesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz())
                .isEqualTo(NodoFiltro.predicado("titulo", FiltroOperador.CONTIENE, "gestión"));
    }
}
