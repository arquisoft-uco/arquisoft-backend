package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarParticipacionesArtefactoRequestMapperTest {

    @Test
    void debeMapearPaginacionYOrden_cuandoHayDto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("estadoUltimaRevision:DESC"));

        // Act
        var query = ConsultarParticipacionesArtefactoRequestMapper.toQuery(dto, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("estadoUltimaRevision");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeAplicarValoresPorDefecto_cuandoDtoEsNulo() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarParticipacionesArtefactoRequestMapper.toQuery(null, estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeLanzarValidacion_cuandoEstudianteEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarParticipacionesArtefactoRequestMapper.toQuery(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores().get(0).codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO));
    }
}
