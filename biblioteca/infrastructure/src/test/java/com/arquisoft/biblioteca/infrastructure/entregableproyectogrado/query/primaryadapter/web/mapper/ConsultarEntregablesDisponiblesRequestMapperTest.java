package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.ConsultarEntregablesDisponiblesRequestDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEntregablesDisponiblesRequestMapperTest {

    @Test
    void debeAplicarPaginaCeroYTamanioDiez_cuandoNoHayCuerpoOFaltanCampos() {
        // Arrange
        var vacio = new ConsultarEntregablesDisponiblesRequestDTO(null, null, null);

        // Act
        var sinCuerpo = ConsultarEntregablesDisponiblesRequestMapper.toQuery(null);
        var sinCampos = ConsultarEntregablesDisponiblesRequestMapper.toQuery(vacio);

        // Assert
        assertThat(sinCuerpo.criterio().pagina()).isZero();
        assertThat(sinCuerpo.criterio().tamanio()).isEqualTo(10);
        assertThat(sinCuerpo.titulo()).isEmpty();
        assertThat(sinCampos.criterio().pagina()).isZero();
        assertThat(sinCampos.criterio().tamanio()).isEqualTo(10);
        assertThat(sinCampos.titulo()).isEmpty();
    }

    @Test
    void debeUsarLosValoresDelCuerpo_cuandoVienenInformados() {
        // Arrange
        var dto = new ConsultarEntregablesDisponiblesRequestDTO(3, 25, " gestión ");

        // Act
        var query = ConsultarEntregablesDisponiblesRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.criterio().pagina()).isEqualTo(3);
        assertThat(query.criterio().tamanio()).isEqualTo(25);
        assertThat(query.titulo()).isEqualTo("gestión");
    }
}
