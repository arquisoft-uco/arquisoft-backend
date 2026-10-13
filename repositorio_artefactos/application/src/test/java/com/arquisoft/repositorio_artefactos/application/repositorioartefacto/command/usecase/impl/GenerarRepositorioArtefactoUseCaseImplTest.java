package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreRepositorioArtefactoExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.GenerarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarRepositorioArtefactoUseCaseImplTest {

    @Mock
    private AdministradorPorIdFinder administradorPorIdFinder;
    @Mock
    private NombreRepositorioArtefactoExisteFinder nombreRepositorioArtefactoExisteFinder;
    @Mock
    private GenerarRepositorioArtefactoValidator validator;
    @Mock
    private RepositorioArtefactoOutputPort outputPort;
    @Mock
    private AppLogger logger;

    private GenerarRepositorioArtefactoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerarRepositorioArtefactoUseCaseImpl(
                administradorPorIdFinder, nombreRepositorioArtefactoExisteFinder, validator, outputPort, logger);
    }

    @Test
    void debeRegistrarYRetornarId_cuandoElAdministradorEstaVigenteYLaCombinacionEsLibre() {
        // Arrange
        var administradorId = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", administradorId);
        var administrador = AdministradorDomain.crear(
                administradorId, "123", "Ana Perez", "ana@uco.edu.co", Instant.now());
        when(administradorPorIdFinder.obtener(administradorId)).thenReturn(administrador);
        when(nombreRepositorioArtefactoExisteFinder.obtener(repositorio.getNombreRepositorio())).thenReturn(false);

        // Act
        var id = useCase.ejecutar(repositorio);

        // Assert
        assertThat(id).isEqualTo(repositorio.getId());
        var orden = inOrder(administradorPorIdFinder, nombreRepositorioArtefactoExisteFinder, validator, outputPort);
        orden.verify(administradorPorIdFinder, times(1)).obtener(administradorId);
        orden.verify(nombreRepositorioArtefactoExisteFinder, times(1)).obtener(repositorio.getNombreRepositorio());
        orden.verify(validator).validar(repositorio, administrador, false);
        orden.verify(outputPort).registrar(any(RepositorioArtefactoEntity.class));
        var captor = ArgumentCaptor.forClass(RepositorioArtefactoEntity.class);
        verify(outputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(repositorio.getId());
        assertThat(captor.getValue().nombreRepositorio()).isEqualTo("Repo");
        assertThat(captor.getValue().descripcion()).isEqualTo("Descripcion");
        assertThat(captor.getValue().administrador()).isEqualTo(administradorId);
        verify(logger).info(any(ClaveMensaje.class), eq(repositorio.getId()));
    }

    @Test
    void debeNoRegistrarNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        var administradorId = UtilUUID.generarNuevoUUID();
        var repositorio = RepositorioArtefactoDomain.crear("Repo", "Descripcion", administradorId);
        when(administradorPorIdFinder.obtener(administradorId)).thenReturn(AdministradorDomain.VACIO);
        when(nombreRepositorioArtefactoExisteFinder.obtener(repositorio.getNombreRepositorio())).thenReturn(false);
        doThrow(new AdministradorNoVigenteException(administradorId))
                .when(validator).validar(repositorio, AdministradorDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(repositorio))
                .isInstanceOf(AdministradorNoVigenteException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(repositorio.getId()));
    }
}
