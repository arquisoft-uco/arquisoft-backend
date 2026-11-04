package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarVersionesRepositorioArtefactoQueryTest {

    @Test
    void debeCrear_cuandoRepositorioValido() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarVersionesRepositorioArtefactoQuery.crear(repositorio);

        // Assert
        assertThat(query.repositorioArtefacto()).isEqualTo(repositorio);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoRepositorioNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarVersionesRepositorioArtefactoQuery.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo", "codigoError")
                        .containsExactly(tuple(
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.REPOSITORIO,
                                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO)));
    }
}
