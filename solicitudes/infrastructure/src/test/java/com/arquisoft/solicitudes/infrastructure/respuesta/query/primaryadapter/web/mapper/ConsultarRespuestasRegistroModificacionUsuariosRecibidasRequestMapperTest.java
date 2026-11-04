package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRespuestasRegistroModificacionUsuariosRecibidasRequestMapperTest {

    @Test
    void debeConstruirQueryConElCoordinadorDelJwt_cuandoElDtoTraeCriterio() {
        // Arrange
        var coordinadorUsuario = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("fechaRespuesta:ASC"));

        // Act
        var query = ConsultarRespuestasRegistroModificacionUsuariosRecibidasRequestMapper.toQuery(
                dto, coordinadorUsuario);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("fechaRespuesta");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeAplicarPorDefecto_cuandoElDtoEsNulo() {
        // Arrange
        var coordinadorUsuario = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarRespuestasRegistroModificacionUsuariosRecibidasRequestMapper.toQuery(
                null, coordinadorUsuario);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().raiz()).isNull();
    }
}
