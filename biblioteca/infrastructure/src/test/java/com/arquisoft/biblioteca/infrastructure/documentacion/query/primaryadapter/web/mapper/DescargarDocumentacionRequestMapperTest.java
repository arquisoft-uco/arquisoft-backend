package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DescargarDocumentacionRequestMapperTest {

    private static IdentidadActual identidad(List<String> roles) {
        return new IdentidadActual(UtilUUID.generarNuevoUUID(), null, null, roles, null, null);
    }

    @Test
    void debeTraducirLosCuatroRolesYLlevarLaDocumentacion_cuandoElTokenLosTrae() {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var actual = identidad(List.of(UsuariosRoles.BIBLIOTECARIO, UsuariosRoles.ASESOR,
                UsuariosRoles.COORDINADOR, UsuariosRoles.ESTUDIANTE, UsuariosRoles.JURADO));

        // Act
        var query = DescargarDocumentacionRequestMapper.toQuery(documentacion, actual);

        // Assert
        assertThat(query.documentacion()).isEqualTo(documentacion);
        assertThat(query.roles()).containsExactlyInAnyOrder(RolConsultaDocumentacion.values());
    }

    @Test
    void debeDevolverRolesVacios_cuandoNoHayRolDeNegocio() {
        // Act
        var query = DescargarDocumentacionRequestMapper.toQuery(UtilUUID.generarNuevoUUID(), identidad(List.of()));

        // Assert
        assertThat(query.roles()).isEmpty();
    }
}
