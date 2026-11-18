package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemAsesorEstudianteRequestMapperTest {

    @Test
    void debeAplicarLosValoresPorDefectoYArmarConIdentidad_cuandoNoLlegaBody() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesItemAsesorEstudianteRequestMapper.toQuery(null, evaluacion, estudiante);

        // Assert
        assertThat(query.evaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarElCriterioConPaginacionOrdenYFiltro_cuandoLlegaBody() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(
                2, 5, List.of("descripcion:desc"), new PredicadoFiltroDTO("descripcion", "CONTIENE", "rigor"));

        // Act
        var query = ConsultarObservacionesItemAsesorEstudianteRequestMapper.toQuery(
                dto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(5);
        assertThat(query.criterio().ordenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.DESC));
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor"));
    }

    @Test
    void debeLanzarValidacion_cuandoLaEvaluacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorEstudianteRequestMapper.toQuery(
                null, null, UtilUUID.generarNuevoUUID()))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
