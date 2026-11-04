package com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorVigenteRuleImplTest {

    private final AdministradorVigenteRuleImpl regla = new AdministradorVigenteRuleImpl();

    private static AdministradorDomain administrador(UUID id, Instant eliminadoEn) {
        return AdministradorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    @Test
    void debeLanzarExcepcion_cuandoElAdministradorNoExisteEnLaReplica() {
        // Arrange
        var vigencia = new VigenciaAdministrador(UtilUUID.generarNuevoUUID(), AdministradorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AdministradorNoVigenteException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_NO_VIGENTE);
    }

    @Test
    void debeLanzarExcepcion_cuandoElAdministradorFueDadoDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAdministrador(id, administrador(id, Instant.parse("2026-09-20T10:00:00Z")));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debePasar_cuandoElAdministradorExisteYEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAdministrador(id, administrador(id, null));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
