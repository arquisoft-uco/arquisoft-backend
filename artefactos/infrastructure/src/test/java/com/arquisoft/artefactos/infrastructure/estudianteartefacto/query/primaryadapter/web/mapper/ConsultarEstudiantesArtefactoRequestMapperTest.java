package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesArtefactoRequestMapperTest {

    @Test
    void debeConstruirQuery_cuandoArtefactoValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEstudiantesArtefactoRequestMapper.toQuery(artefacto);

        // Assert
        assertThat(query.artefacto()).isEqualTo(artefacto);
    }

    @Test
    void debeLanzar400_cuandoArtefactoNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesArtefactoRequestMapper.toQuery(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores().get(0).codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO));
    }
}
