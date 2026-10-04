package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesItemEstudianteRequestMapperTest {

    @Test
    void debeMapearDtoYEstudianteAQuery_cuandoDtoTieneOrden() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("estadoObservacionRevision:DESC"));

        // Act
        var query = ConsultarObservacionesItemEstudianteRequestMapper.toQuery(dto, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).singleElement().satisfies(orden -> {
            assertThat(orden.getCampo()).isEqualTo("estadoObservacionRevision");
            assertThat(orden.getDireccion()).isEqualTo(SortDirection.DESC);
        });
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var estudiante = UUID.randomUUID();

        // Act
        var query = ConsultarObservacionesItemEstudianteRequestMapper.toQuery(null, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoElEstudianteEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemEstudianteRequestMapper.toQuery(null, null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
