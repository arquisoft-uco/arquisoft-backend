package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarParticipacionesArtefactoRequestMapperTest {

    @Test
    void debeConstruirElQuery_cuandoEstudianteValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarParticipacionesArtefactoRequestMapper.toQuery(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarValidacion_cuandoEstudianteEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarParticipacionesArtefactoRequestMapper.toQuery(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores().get(0).codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_ID_REQUERIDO));
    }
}
