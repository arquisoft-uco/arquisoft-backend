package com.arquisoft.shared.postgres.util;

import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.pagination.SortDirection;
import com.arquisoft.shared.query.QueryCriteria;
import com.arquisoft.shared.query.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageableMapperTest {

    private static final UnaryOperator<String> TRADUCTOR =
            Map.of("titulo", "tituloProyecto", "asesor", "asesorNombre")::get;

    private static final class CriteriaDePrueba extends QueryCriteria {

        private CriteriaDePrueba(Builder b) {
            super(b);
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
            CriteriaDePrueba build() {
                return new CriteriaDePrueba(this);
            }
        }
    }

    @Test
    void debeConstruirPageableSinOrden_cuandoElCriteriaNoTraeOrdenamiento() {
        // Arrange
        QueryCriteria criteria = CriteriaDePrueba.builder().pagina(2).tamanio(25).build();

        // Act
        Pageable pageable = PageableMapper.toPageable(criteria, TRADUCTOR);

        // Assert
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(25);
        assertThat(pageable.getSort().isSorted()).isFalse();
    }

    @Test
    void debeTraducirLaClavePublicaALaRutaJpa_cuandoElCriteriaTraeOrdenamiento() {
        // Arrange
        QueryCriteria criteria = CriteriaDePrueba.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("asesor", SortDirection.DESC)))
                .build();

        // Act
        Pageable pageable = PageableMapper.toPageable(criteria, TRADUCTOR);

        // Assert
        Sort.Order orden = pageable.getSort().getOrderFor("asesorNombre");
        assertThat(orden).isNotNull();
        assertThat(orden.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void debeConservarElOrdenDeVariosCampos_cuandoElCriteriaTraeMasDeUno() {
        // Arrange
        QueryCriteria criteria = CriteriaDePrueba.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(
                        SortOrder.of("titulo", SortDirection.ASC),
                        SortOrder.of("asesor", SortDirection.DESC)))
                .build();

        // Act
        Pageable pageable = PageableMapper.toPageable(criteria, TRADUCTOR);

        // Assert
        assertThat(pageable.getSort())
                .extracting(Sort.Order::getProperty)
                .containsExactly("tituloProyecto", "asesorNombre");
    }

    // Un campo que el traductor no resuelve no es error del cliente: el Criteria ya rechazo todo
    // campo que no declare ordenable. Llegar aqui significa que el SortMapper de la feature y el
    // Criteria divergieron, asi que debe aflorar como defecto (500) y no como un 4xx que le diria
    // al cliente que su campo es invalido cuando no lo es.
    @Test
    void debeLanzarDefectoDeMapeo_cuandoElTraductorNoResuelveLaClave() {
        // Arrange
        QueryCriteria criteria = CriteriaDePrueba.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(List.of(SortOrder.of("desconocido", SortDirection.ASC)))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> PageableMapper.toPageable(criteria, TRADUCTOR))
                .isInstanceOf(IllegalStateException.class)
                .isNotInstanceOf(BaseException.class)
                .hasMessageContaining("desconocido");
    }
}
