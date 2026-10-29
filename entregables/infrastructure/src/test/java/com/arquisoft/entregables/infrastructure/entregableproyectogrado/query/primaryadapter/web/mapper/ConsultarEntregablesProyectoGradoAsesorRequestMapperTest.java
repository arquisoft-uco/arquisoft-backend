package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEntregablesProyectoGradoAsesorRequestMapperTest {

    @Test
    void debeAplicarValoresPorDefecto_cuandoElDtoEsNulo() {
        // Act
        var query = ConsultarEntregablesProyectoGradoAsesorRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.ordenamiento()).isEmpty();
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeConstruirLaQuery_conOrden() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(3, 15, List.of("versionEntregable:desc"), null);

        // Act
        var query = ConsultarEntregablesProyectoGradoAsesorRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(3);
        assertThat(query.tamanio()).isEqualTo(15);
        assertThat(query.ordenamiento()).hasSize(1);
        assertThat(query.ordenamiento().get(0).getCampo()).isEqualTo("versionEntregable");
        assertThat(query.ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
