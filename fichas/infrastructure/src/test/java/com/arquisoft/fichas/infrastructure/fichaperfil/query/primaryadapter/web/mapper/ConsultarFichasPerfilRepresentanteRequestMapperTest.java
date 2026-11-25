package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarFichasPerfilRepresentanteRequestMapperTest {

    @Test
    void debeUsarValoresPorDefecto_cuandoNoHayBody() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarFichasPerfilRepresentanteRequestMapper.toQuery(null, representante);

        // Assert
        assertThat(query.representanteComite()).isEqualTo(representante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarElQueryConPaginacionOrdenYRepresentante_cuandoHayBody() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("asesorNombre:DESC"));

        // Act
        var query = ConsultarFichasPerfilRepresentanteRequestMapper.toQuery(dto, representante);

        // Assert
        assertThat(query.representanteComite()).isEqualTo(representante);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("asesorNombre");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
