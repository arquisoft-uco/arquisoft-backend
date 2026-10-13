package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarSolicitudesCambioAsesorEnviadasRequestMapperTest {

    @Test
    void debeAplicarDefaults_cuandoElDtoEsNulo() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarSolicitudesCambioAsesorEnviadasRequestMapper.toQuery(null, estudiante);

        // Assert
        assertThat(query.estudianteUsuario()).isEqualTo(estudiante);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeConstruirElQueryConElEstudianteYElCriterio_cuandoElDtoTraePaginacionYFiltros() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var filtro = new PredicadoFiltroDTO("destinatarioNombre", "CONTIENE", "Coord");
        var dto = new QueryCriteriaRequestDTO(2, 25, List.of("fechaCreacion:ASC"), filtro);

        // Act
        var query = ConsultarSolicitudesCambioAsesorEnviadasRequestMapper.toQuery(dto, estudiante);

        // Assert
        assertThat(query.estudianteUsuario()).isEqualTo(estudiante);
        assertThat(query.criterio().raiz()).isEqualTo(filtro.toDomain());
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(25);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
    }
}
