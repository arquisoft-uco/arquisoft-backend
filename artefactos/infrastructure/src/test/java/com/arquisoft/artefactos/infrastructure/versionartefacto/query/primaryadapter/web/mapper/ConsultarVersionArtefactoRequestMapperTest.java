package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarVersionArtefactoRequestMapperTest {

    private static IdentidadActual identidad(List<String> roles) {
        return new IdentidadActual(UtilUUID.generarNuevoUUID(), null, null, roles, null, null);
    }

    @Test
    void debeTraducirRoles_cuandoTokenTraeAsesorYEstudiante() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var actual = identidad(List.of(UsuariosRoles.ASESOR, UsuariosRoles.ESTUDIANTE, "coordinador"));

        // Act
        var query = ConsultarVersionArtefactoRequestMapper.toQuery(version, actual);

        // Assert
        assertThat(query.versionArtefacto()).isEqualTo(version);
        assertThat(query.actor()).isEqualTo(actual.id());
        assertThat(query.roles()).containsExactlyInAnyOrder(
                RolConsultaVersionArtefacto.ASESOR, RolConsultaVersionArtefacto.ESTUDIANTE);
    }

    @Test
    void debeLanzar400_cuandoTokenSinRolesDeNegocio() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var actual = identidad(List.of("coordinador"));

        // Act & Assert
        assertThatThrownBy(() -> ConsultarVersionArtefactoRequestMapper.toQuery(version, actual))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
