package com.arquisoft.artefactos.application.artefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudiantesSuperanProyectoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactivarArtefactoValidatorImplTest {

    private final ReactivarArtefactoValidatorImpl validator = new ReactivarArtefactoValidatorImpl();

    private UUID proyecto;
    private UUID asesorId;
    private UUID estudiante;
    private ReactivacionArtefactoDomain reactivacion;
    private AsesorDomain asesorVigente;
    private AsesorProyectoGradoDomain vinculoVigente;
    private CupoEstudiantesArtefacto cupoValido;

    @BeforeEach
    void setUp() {
        proyecto = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
        reactivacion = ReactivacionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), proyecto, asesorId, List.of(estudiante));
        asesorVigente = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
        vinculoVigente = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesorId, proyecto, UtilFecha.generarInstanteActual(), null);
        cupoValido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of(estudiante));
    }

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(reactivacion, asesorVigente, List.of(vinculoVigente), cupoValido))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoEncontrado_antesQueLasDemasReglas_cuandoTodoFalla() {
        // Arrange
        var cupoExcedido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, AsesorDomain.VACIO, List.of(), cupoExcedido))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAsesorNoAsignado_antesQueElCupo_cuandoOtroTieneElVinculoMasReciente() {
        // Arrange
        var otroAsesor = AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), proyecto,
                UtilFecha.generarInstanteActual(), null);
        var cupoExcedido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, asesorVigente, List.of(otroAsesor), cupoExcedido))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzarEstudiantesSuperanProyecto_cuandoElCupoExcedeAlProyecto() {
        // Arrange
        var vinculado = UtilUUID.generarNuevoUUID();
        var cupoExcedido = new CupoEstudiantesArtefacto(List.of(estudiante), List.of(vinculado), List.of(vinculado));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(reactivacion, asesorVigente, List.of(vinculoVigente), cupoExcedido))
                .isInstanceOf(EstudiantesSuperanProyectoException.class);
    }
}
