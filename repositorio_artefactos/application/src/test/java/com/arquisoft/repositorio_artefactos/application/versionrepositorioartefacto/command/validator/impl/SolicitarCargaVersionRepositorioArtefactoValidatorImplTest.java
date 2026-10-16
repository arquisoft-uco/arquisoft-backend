package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitarCargaVersionRepositorioArtefactoValidatorImplTest {

    private static final long MAXIMO = 5_242_880L;

    private final SolicitarCargaVersionRepositorioArtefactoValidatorImpl validator =
            new SolicitarCargaVersionRepositorioArtefactoValidatorImpl();

    private SolicitudCargaVersionRepositorioArtefactoDomain solicitud;
    private AdministradorDomain administrador;
    private RepositorioArtefactoDomain repositorio;

    @BeforeEach
    void setUp() {
        solicitud = SolicitudCargaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", "application/pdf", 2048L);
        administrador = AdministradorDomain.crear(
                solicitud.getSolicitante(), "123", "Ana", "ana@uco.edu.co", Instant.now());
        repositorio = RepositorioArtefactoDomain.reconstruir(
                solicitud.getRepositorio(), "Repo", "Desc", UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(solicitud, administrador, repositorio, MAXIMO))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAdministradorNoVigente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, AdministradorDomain.VACIO, repositorio, MAXIMO))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debeLanzar_cuandoRepositorioNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, administrador, RepositorioArtefactoDomain.VACIO, MAXIMO))
                .isInstanceOf(RepositorioArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoTamanoDeclaradoExcede() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, administrador, repositorio, 1024L))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }

    @Test
    void debePriorizar_administradorAntesQueRepositorioYTamano() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, AdministradorDomain.VACIO,
                RepositorioArtefactoDomain.VACIO, 1L))
                .isInstanceOf(AdministradorNoVigenteException.class);
    }
}
