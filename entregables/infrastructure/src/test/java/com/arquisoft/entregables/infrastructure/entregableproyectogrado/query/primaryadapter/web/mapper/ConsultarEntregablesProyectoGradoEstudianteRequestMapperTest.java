package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEntregablesProyectoGradoEstudianteRequestMapperTest {

    private static IdentidadActual identidad(UUID id) {
        return new IdentidadActual(id, "est@uco.edu.co", "est", List.of(), null, null);
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoElDtoEsNulo() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEntregablesProyectoGradoEstudianteRequestMapper.toQuery(
                null, proyecto, identidad(estudiante));

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.proyectoGrado()).isEqualTo(proyecto);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeConstruirLaQuery_conIdentidadProyectoYOrden() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO(3, 15, List.of("versionEntregable:desc"), null);

        // Act
        var query = ConsultarEntregablesProyectoGradoEstudianteRequestMapper.toQuery(
                dto, proyecto, identidad(estudiante));

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.proyectoGrado()).isEqualTo(proyecto);
        assertThat(query.criterio().pagina()).isEqualTo(3);
        assertThat(query.criterio().tamanio()).isEqualTo(15);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("versionEntregable");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
