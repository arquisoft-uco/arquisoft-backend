package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarDocumentacionesRequestMapperTest {

    private static IdentidadActual identidad(List<String> roles) {
        return new IdentidadActual(UtilUUID.generarNuevoUUID(), null, null, roles, null, null);
    }

    @Test
    void debeTraducirLosCuatroRoles_cuandoElTokenLosTrae() {
        // Arrange
        var actual = identidad(List.of(UsuariosRoles.BIBLIOTECARIO, UsuariosRoles.ASESOR,
                UsuariosRoles.COORDINADOR, UsuariosRoles.ESTUDIANTE));

        // Act
        var query = ConsultarDocumentacionesRequestMapper.toQuery(null, actual);

        // Assert
        assertThat(query.roles()).containsExactlyInAnyOrder(RolConsultaDocumentacion.values());
    }

    @Test
    void debeIgnorarRolesDesconocidos_cuandoElTokenTraeOtros() {
        // Arrange
        var actual = identidad(List.of(UsuariosRoles.JURADO, UsuariosRoles.ESTUDIANTE));

        // Act
        var query = ConsultarDocumentacionesRequestMapper.toQuery(null, actual);

        // Assert
        assertThat(query.roles()).containsExactly(RolConsultaDocumentacion.ESTUDIANTE);
    }

    @Test
    void debeProducirListaVacia_cuandoNoHayRoles() {
        // Act
        var query = ConsultarDocumentacionesRequestMapper.toQuery(null, identidad(List.of()));

        // Assert
        assertThat(query.roles()).isEmpty();
    }

    @Test
    void debeAplicarPorDefectoYConstruirElCriterio_cuandoElBodyEsNuloOTrae() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(2, 15, List.of("fechaPublicacion:asc"), null);

        // Act
        var porDefecto = ConsultarDocumentacionesRequestMapper.toQuery(null, identidad(List.of()));
        var conDto = ConsultarDocumentacionesRequestMapper.toQuery(dto, identidad(List.of()));

        // Assert
        assertThat(porDefecto.criterio().pagina()).isZero();
        assertThat(porDefecto.criterio().tamanio()).isEqualTo(10);
        assertThat(porDefecto.criterio().ordenamiento()).isEmpty();
        assertThat(porDefecto.criterio().raiz()).isNull();
        assertThat(conDto.criterio().pagina()).isEqualTo(2);
        assertThat(conDto.criterio().tamanio()).isEqualTo(15);
        assertThat(conDto.criterio().ordenamiento().get(0).getCampo()).isEqualTo("fechaPublicacion");
    }
}
