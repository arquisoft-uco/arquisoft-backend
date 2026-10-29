package com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosPosiblesAsesorRequestMapperTest {

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Act
        var query = ConsultarEstadosPosiblesAsesorRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeMapearPaginacionYOrden_cuandoDtoCompleto() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("nombre:DESC"));

        // Act
        var query = ConsultarEstadosPosiblesAsesorRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(1);
        assertThat(query.tamanio()).isEqualTo(20);
        assertThat(query.ordenamiento().getFirst().getCampo()).isEqualTo("nombre");
        assertThat(query.ordenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
