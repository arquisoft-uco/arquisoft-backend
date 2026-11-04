package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactivacionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var reactivacion = ReactivacionArtefactoDomain.crear(artefacto, proyecto, asesor, List.of(estudiante));

        // Assert
        assertThat(reactivacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(reactivacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(reactivacion.getAsesor()).isEqualTo(asesor);
        assertThat(reactivacion.getEstudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanTodosLosDatos() {
        // Act & Assert
        assertThatThrownBy(() -> ReactivacionArtefactoDomain.crear(null, null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.ID,
                            ArtefactosFields.Artefacto.PROYECTO_GRADO,
                            ArtefactosFields.Artefacto.ASESOR,
                            ArtefactosFields.EstudianteArtefacto.ESTUDIANTES);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.Artefacto.ID_REQUERIDO,
                            ArtefactosCodes.Artefacto.PROYECTO_GRADO_REQUERIDO,
                            ArtefactosCodes.Artefacto.ASESOR_REQUERIDO,
                            ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS);
                });
    }
}
