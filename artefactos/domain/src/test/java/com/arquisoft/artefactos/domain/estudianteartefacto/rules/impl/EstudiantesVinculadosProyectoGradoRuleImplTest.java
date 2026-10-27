package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ParEstudianteArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.PertenenciaProyectoGradoEstudiantes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesVinculadosProyectoGradoRuleImplTest {

    private static final String NOMBRE = "Laura Gomez";

    private final EstudiantesVinculadosProyectoGradoRuleImpl rule = new EstudiantesVinculadosProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoTodosLosParesTienenVinculoVigente() {
        // Arrange
        var par = new ParEstudianteArtefacto(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var pertenencia = new PertenenciaProyectoGradoEstudiantes(List.of(), List.of(par), List.of(par));

        // Act & Assert
        assertThatCode(() -> rule.validar(pertenencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConNombreSinUuid_cuandoUnParNoTieneVinculoVigente() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var conocido = EstudianteDomain.reconstruir(estudiante, "1", NOMBRE, "l@uco.edu.co", null);
        var pertenencia = new PertenenciaProyectoGradoEstudiantes(
                List.of(conocido), List.of(new ParEstudianteArtefacto(artefacto, estudiante)), List.of());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(pertenencia))
                .isInstanceOf(EstudianteNoVinculadoProyectoGradoException.class)
                .satisfies(ex -> {
                    var excepcion = (EstudianteNoVinculadoProyectoGradoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTE_NO_VINCULADO_PROYECTO_GRADO);
                    assertThat(excepcion.getError().getMessage()).contains(NOMBRE).doesNotContain(estudiante.toString());
                });
    }
}
