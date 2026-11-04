package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EstudianteArtefactoDomainTest {

    @Test
    void debeCrearUnaRelacionPorEstudiante_cuandoHayVariosEstudiantes() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var relaciones = EstudianteArtefactoDomain.crear(artefacto, estudiantes);

        // Assert
        assertThat(relaciones).hasSize(3);
        assertThat(relaciones).extracting(EstudianteArtefactoDomain::getId).doesNotHaveDuplicates().doesNotContainNull();
        assertThat(relaciones).extracting(EstudianteArtefactoDomain::getArtefacto).containsOnly(artefacto);
        assertThat(relaciones).extracting(EstudianteArtefactoDomain::getEstudiante)
                .containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeLanzar_cuandoLaListaDeEstudiantesEstaVacia() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiantesVacios = List.<UUID>of();

        // Act & Assert
        assertThatThrownBy(() -> EstudianteArtefactoDomain.crear(artefacto, estudiantesVacios))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactly(tuple(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                                ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS)));
    }

    @Test
    void debeLanzar_cuandoElArtefactoEsNulo() {
        // Arrange
        var estudiantes = List.of(UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> EstudianteArtefactoDomain.crear(null, estudiantes))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactly(tuple(ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO)));
    }

    @Test
    void debeRechazarEstudiantesDuplicados_cuandoSeRepiteUnId() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiantes = List.of(repetido, repetido);

        // Act & Assert
        assertThatThrownBy(() -> EstudianteArtefactoDomain.crear(artefacto, estudiantes))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_DUPLICADOS);
                        }));
    }
}
