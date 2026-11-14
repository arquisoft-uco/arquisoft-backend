package com.arquisoft.mapas_ruta.infrastructure.planreal.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarPlanesRealesProyectoGradoAsesorRequestMapperTest {

    @Test
    void debeArmarQuery_cuandoDtoEsNulo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarPlanesRealesProyectoGradoAsesorRequestMapper.toQuery(null, proyectoGrado);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarQuery_cuandoDtoTraeFiltrosYOrden() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("descripcionTarea:DESC"));
        dto.setFiltros(new PredicadoFiltroDTO("descripcionReal", "CONTIENE", "rev"));

        // Act
        var query = ConsultarPlanesRealesProyectoGradoAsesorRequestMapper.toQuery(dto, proyectoGrado);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("descripcionTarea");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("descripcionReal", FiltroOperador.CONTIENE, "rev"));
    }
}
