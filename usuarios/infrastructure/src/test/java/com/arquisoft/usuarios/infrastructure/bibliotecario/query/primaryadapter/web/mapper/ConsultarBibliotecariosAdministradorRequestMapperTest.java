package com.arquisoft.usuarios.infrastructure.bibliotecario.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarBibliotecariosAdministradorRequestMapperTest {

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoNulo() {
        // Act
        var query = ConsultarBibliotecariosAdministradorRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.ordenamiento()).isEmpty();
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeTraducirPaginacionYOrden_cuandoElBodyLosTrae() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(2, 25, List.of("email:DESC"), null);

        // Act
        var query = ConsultarBibliotecariosAdministradorRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(2);
        assertThat(query.tamanio()).isEqualTo(25);
        assertThat(query.ordenamiento()).hasSize(1);
        assertThat(query.ordenamiento().get(0).getCampo()).isEqualTo("email");
        assertThat(query.raiz()).isNull();
    }
}
