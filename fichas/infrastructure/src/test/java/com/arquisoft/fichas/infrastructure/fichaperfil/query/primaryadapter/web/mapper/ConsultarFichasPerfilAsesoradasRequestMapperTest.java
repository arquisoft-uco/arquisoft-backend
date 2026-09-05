package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarFichasPerfilAsesoradasRequestMapperTest {

    @Test
    void debeMapearDtoYAsesorFichaAQuery_cuandoDtoTieneFiltrosYOrden() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("tituloProyecto:DESC"));

        // Act
        var query = ConsultarFichasPerfilAsesoradasRequestMapper.toQuery(dto, asesorFicha);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("tituloProyecto");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var asesorFicha = UUID.randomUUID();

        // Act
        var query = ConsultarFichasPerfilAsesoradasRequestMapper.toQuery(null, asesorFicha);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }
}
