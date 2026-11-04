package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoDuplicadoException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRepositorioArtefactoValidatorImplTest {

    private final ModificarRepositorioArtefactoValidatorImpl validator = new ModificarRepositorioArtefactoValidatorImpl();

    private static ModificacionRepositorioArtefactoDomain modificacion() {
        return ModificacionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), "Repo", "Descripcion", UtilUUID.generarNuevoUUID());
    }

    private static AdministradorDomain administradorVigente(ModificacionRepositorioArtefactoDomain modificacion) {
        return AdministradorDomain.reconstruir(modificacion.getAdministrador(), "123", "Ana Perez",
                "ana@uco.edu.co", Instant.now(), null);
    }

    private static RepositorioArtefactoDomain repositorioExistente(ModificacionRepositorioArtefactoDomain modificacion) {
        return RepositorioArtefactoDomain.reconstruir(
                modificacion.getRepositorio(), "Repo", "Descripcion", UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Arrange
        var modificacion = modificacion();
        var administrador = administradorVigente(modificacion);
        var repositorio = repositorioExistente(modificacion);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, administrador, repositorio, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAdministradorNoVigente_cuandoAdministradorVacio() {
        // Arrange
        var modificacion = modificacion();
        var repositorio = repositorioExistente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, AdministradorDomain.VACIO, repositorio, false))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoRepositorioVacio() {
        // Arrange
        var modificacion = modificacion();
        var administrador = administradorVigente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, administrador, RepositorioArtefactoDomain.VACIO, false))
                .isInstanceOf(RepositorioArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarDuplicado_cuandoNombreEnOtroRepositorio() {
        // Arrange
        var modificacion = modificacion();
        var administrador = administradorVigente(modificacion);
        var repositorio = repositorioExistente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, administrador, repositorio, true))
                .isInstanceOf(RepositorioArtefactoDuplicadoException.class);
    }

    @Test
    void debePriorizarAdministrador_cuandoFallanVarias() {
        // Arrange
        var modificacion = modificacion();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                modificacion, AdministradorDomain.VACIO, RepositorioArtefactoDomain.VACIO, true))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debePriorizarExistencia_cuandoRepositorioVacioYNombreDuplicado() {
        // Arrange
        var modificacion = modificacion();
        var administrador = administradorVigente(modificacion);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, administrador, RepositorioArtefactoDomain.VACIO, true))
                .isInstanceOf(RepositorioArtefactoNoEncontradoException.class);
    }
}
