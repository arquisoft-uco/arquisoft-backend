package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRepositoriosArtefactoRequestMapperTest {

    @Test
    void debeAplicarValoresPorDefecto_cuandoElDtoEsNulo() {
        // Act
        var query = ConsultarRepositoriosArtefactoRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.ordenamiento()).isEmpty();
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeCopiarPaginaTamanioYOrden_cuandoElDtoLosTrae() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(2, 30, List.of("descripcion:DESC"), null);

        // Act
        var query = ConsultarRepositoriosArtefactoRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(2);
        assertThat(query.tamanio()).isEqualTo(30);
        assertThat(query.ordenamiento()).hasSize(1);
    }
}
