package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescargarArchivoVersionArtefactoQueryTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();

        // Act
        var query = DescargarArchivoVersionArtefactoQuery.crear(
                version, actor, List.of(RolConsultaVersionArtefacto.ESTUDIANTE));

        // Assert
        assertThat(query.versionArtefacto()).isEqualTo(version);
        assertThat(query.actor()).isEqualTo(actor);
        assertThat(query.roles()).containsExactly(RolConsultaVersionArtefacto.ESTUDIANTE);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltaVersionActorORoles() {
        // Act & Assert
        assertThatThrownBy(() -> DescargarArchivoVersionArtefactoQuery.crear(null, null, List.of()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            ArtefactosFields.VersionArtefacto.VERSION_ARTEFACTO,
                            ArtefactosFields.VersionArtefacto.ACTOR,
                            ArtefactosFields.VersionArtefacto.ROLES);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA,
                            ArtefactosCodes.VersionArtefacto.ACTOR_REQUERIDO,
                            ArtefactosCodes.VersionArtefacto.ROLES_REQUERIDOS);
                });
    }
}
