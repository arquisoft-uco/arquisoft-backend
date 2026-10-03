package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRevisionesItemEstudianteRequestMapperTest {

    @Test
    void debeMapearDtoYEstudianteAQuery_cuandoDtoTieneFiltrosYOrden() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("estadoRevision:DESC"));

        // Act
        var query = ConsultarRevisionesItemEstudianteRequestMapper.toQuery(dto, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("estadoRevision");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarRevisionesItemEstudianteRequestMapper.toQuery(null, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }
}
