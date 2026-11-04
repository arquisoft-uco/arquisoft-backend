package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoDuplicadoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarRepositorioArtefactoValidatorImplTest {

    private final GenerarRepositorioArtefactoValidatorImpl validator = new GenerarRepositorioArtefactoValidatorImpl();

    private static AdministradorDomain administrador(UUID id, Instant eliminadoEn) {
        return AdministradorDomain.reconstruir(id, "123", "Ana Perez", "ana@uco.edu.co", Instant.now(), eliminadoEn);
    }

    @Test
    void debePasar_cuandoElAdministradorEstaVigenteYLaCombinacionEsLibre() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", id);

        // Act & Assert
        assertThatCode(() -> validator.validar(repositorio, administrador(id, null), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazarAdministrador_cuandoEstaAusenteOEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", id);
        var eliminado = administrador(id, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(repositorio, AdministradorDomain.VACIO, false))
                .isInstanceOf(AdministradorNoVigenteException.class);
        assertThatThrownBy(() -> validator.validar(repositorio, eliminado, false))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debeRechazarDuplicado_cuandoYaExisteLaCombinacion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", id);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(repositorio, administrador(id, null), true))
                .isInstanceOf(RepositorioArtefactoDuplicadoException.class);
    }

    @Test
    void debePriorizarAdministrador_cuandoAmbasReglasFallan() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", id);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(repositorio, AdministradorDomain.VACIO, true))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }
}
