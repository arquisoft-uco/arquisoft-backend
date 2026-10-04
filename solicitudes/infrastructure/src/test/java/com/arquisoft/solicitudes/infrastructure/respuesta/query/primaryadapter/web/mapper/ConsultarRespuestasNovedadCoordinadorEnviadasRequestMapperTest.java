package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRespuestasNovedadCoordinadorEnviadasRequestMapperTest {

    @Test
    void debeMapearDtoYCoordinadorUsuarioAQuery_cuandoDtoTieneFiltrosYOrden() {
        // Arrange
        var coordinadorUsuario = UUID.randomUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("fechaRespuesta:ASC"));

        // Act
        var query = ConsultarRespuestasNovedadCoordinadorEnviadasRequestMapper.toQuery(
                dto, coordinadorUsuario);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("fechaRespuesta");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var coordinadorUsuario = UUID.randomUUID();

        // Act
        var query = ConsultarRespuestasNovedadCoordinadorEnviadasRequestMapper.toQuery(
                null, coordinadorUsuario);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }
}
