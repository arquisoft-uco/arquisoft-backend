package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarPlanesEstimadosTareaEstudianteRequestMapperTest {

    @Test
    void debeComponerQuery_cuandoDtoTraeFiltros() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("tiempoEstimadoDias:DESC"));
        dto.setFiltros(new PredicadoFiltroDTO("descripcionEstimado", "CONTIENE", "rev"));

        // Act
        var query = ConsultarPlanesEstimadosTareaEstudianteRequestMapper.toQuery(dto, tarea);

        // Assert
        assertThat(query.tarea()).isEqualTo(tarea);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("tiempoEstimadoDias");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("descripcionEstimado", FiltroOperador.CONTIENE, "rev"));
    }

    @Test
    void debeAplicarCriterioPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarPlanesEstimadosTareaEstudianteRequestMapper.toQuery(null, tarea);

        // Assert
        assertThat(query.tarea()).isEqualTo(tarea);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }
}
