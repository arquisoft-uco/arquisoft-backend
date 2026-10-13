package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarProyectosGradoCoordinadorRequestMapperTest {

    @Test
    void debeArmarQueryConDefaults_cuandoDtoNulo() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarProyectosGradoCoordinadorRequestMapper.toQuery(null, coordinador);

        // Assert
        assertThat(query.coordinador()).isEqualTo(coordinador);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarQueryConCoordinadorYOrden_cuandoDtoTrae() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("tituloProyecto:DESC"));

        // Act
        var query = ConsultarProyectosGradoCoordinadorRequestMapper.toQuery(dto, coordinador);

        // Assert
        assertThat(query.coordinador()).isEqualTo(coordinador);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento().getFirst().getCampo()).isEqualTo("tituloProyecto");
        assertThat(query.criterio().ordenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
