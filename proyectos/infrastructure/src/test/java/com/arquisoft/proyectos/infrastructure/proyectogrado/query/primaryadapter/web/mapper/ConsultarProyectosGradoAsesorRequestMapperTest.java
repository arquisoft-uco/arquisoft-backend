package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarProyectosGradoAsesorRequestMapperTest {

    @Test
    void debeArmarQueryConDefaults_cuandoDtoNulo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarProyectosGradoAsesorRequestMapper.toQuery(null, asesor);

        // Assert
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarQueryConAsesorYOrden_cuandoDtoTrae() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("tituloProyecto:DESC"));

        // Act
        var query = ConsultarProyectosGradoAsesorRequestMapper.toQuery(dto, asesor);

        // Assert
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento().getFirst().getCampo()).isEqualTo("tituloProyecto");
        assertThat(query.criterio().ordenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeLanzarValidacion_cuandoAsesorNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarProyectosGradoAsesorRequestMapper.toQuery(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactly(ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO));
    }
}
