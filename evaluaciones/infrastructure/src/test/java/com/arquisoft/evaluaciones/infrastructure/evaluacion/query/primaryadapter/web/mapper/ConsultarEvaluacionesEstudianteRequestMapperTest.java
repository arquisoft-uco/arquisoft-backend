package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesEstudianteRequestMapperTest {

    @Test
    void debeAplicarDefaultsYConservarElSub_cuandoNoLlegaBody() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesEstudianteRequestMapper.toQuery(null, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarLaQueryConPaginacionOrdenYFiltro_cuandoLlegaBody() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO(
                2, 5, List.of("estado:desc"), new PredicadoFiltroDTO("proyecto", "CONTIENE", "robot"));

        // Act
        var query = ConsultarEvaluacionesEstudianteRequestMapper.toQuery(dto, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(5);
        assertThat(query.criterio().ordenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("estado", SortDirection.DESC));
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot"));
    }
}
