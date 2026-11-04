package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ConsultarEvaluacionesFichaPerfilAsesorRequestMapperTest {

    @Test
    void debeMapearDtoYAsesorFichaAQuery_cuandoDtoTraeFiltrosYOrden() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("fechaCreacion:DESC"));
        dto.setFiltros(new PredicadoFiltroDTO("estadoEvaluacion", "ES", "APROBADA"));

        // Act
        var query = ConsultarEvaluacionesFichaPerfilAsesorRequestMapper.toQuery(dto, asesorFicha);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(query.criterio().raiz()).isEqualTo(
                NodoFiltro.predicado("estadoEvaluacion", FiltroOperador.ES, "APROBADA"));
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesFichaPerfilAsesorRequestMapper.toQuery(null, asesorFicha);

        // Assert
        assertThat(query.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoAsesorFichaEsNulo() {
        // Act
        var ex = catchThrowableOfType(ApplicationValidationException.class,
                () -> ConsultarEvaluacionesFichaPerfilAsesorRequestMapper.toQuery(null, null));

        // Assert
        assertThat(ex.getValidationResult().getErrores())
                .singleElement()
                .satisfies(e -> assertThat(e.codigoError())
                        .isEqualTo(FichasCodes.EvaluacionFichaPerfil.ASESOR_FICHA_REQUERIDO));
    }
}
