package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarSolicitudesRegistroModificacionUsuariosRecibidasRequestMapperTest {

    @Test
    void debeAplicarDefaults_cuandoElDtoEsNulo() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarSolicitudesRegistroModificacionUsuariosRecibidasRequestMapper.toQuery(null, administrador);

        // Assert
        assertThat(query.administradorUsuario()).isEqualTo(administrador);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeConstruirElQueryConElAdministradorYElCriterio_cuandoElDtoTraePaginacionYFiltros() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var filtro = new PredicadoFiltroDTO("remitenteNombre", "CONTIENE", "Ana");
        var dto = new QueryCriteriaRequestDTO(2, 25, List.of("fechaCreacion:ASC"), filtro);

        // Act
        var query = ConsultarSolicitudesRegistroModificacionUsuariosRecibidasRequestMapper.toQuery(dto, administrador);

        // Assert
        assertThat(query.administradorUsuario()).isEqualTo(administrador);
        assertThat(query.criterio().raiz()).isEqualTo(filtro.toDomain());
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(25);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
    }
}
