package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarSolicitudesAmpliacionPlazoRecibidasRequestMapperTest {

    @Test
    void debeAplicarPaginacionPorDefectoYConservarElCoordinador_cuandoElDtoEsNulo() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarSolicitudesAmpliacionPlazoRecibidasRequestMapper.toQuery(null, coordinador);

        // Assert
        assertThat(query.coordinadorUsuario()).isEqualTo(coordinador);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_cuandoElDtoLosTrae() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(2, 25, List.of("fechaCreacion:ASC"), null);

        // Act
        var query = ConsultarSolicitudesAmpliacionPlazoRecibidasRequestMapper.toQuery(
                dto, UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(25);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
    }
}
