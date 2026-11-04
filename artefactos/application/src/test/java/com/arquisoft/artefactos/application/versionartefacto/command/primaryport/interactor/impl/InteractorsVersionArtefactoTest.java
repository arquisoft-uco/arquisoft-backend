package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.SolicitarCargaVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.GenerarVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.SolicitarCargaVersionArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractorsVersionArtefactoTest {

    @Mock
    private SolicitarCargaVersionArtefactoUseCase solicitarCargaUseCase;
    @Mock
    private GenerarVersionArtefactoUseCase generarVersionUseCase;

    @Test
    void debeDelegarSolicitarCarga_conElDomainMapeadoDelCommand() {
        // Arrange
        var interactor = new SolicitarCargaVersionArtefactoInteractorImpl(solicitarCargaUseCase);
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var command = SolicitarCargaVersionArtefactoCommand.crear(
                artefacto, solicitante, "informe.pdf", "application/pdf", 10L);
        var esperado = new CargaVersionArtefactoResult(
                UtilUUID.generarNuevoUUID(), "http://minio", Map.of("k", "v"), Instant.now());
        when(solicitarCargaUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(SolicitudCargaVersionArtefactoDomain.class);
        verify(solicitarCargaUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getSolicitante()).isEqualTo(solicitante);
        assertThat(captor.getValue().getNombreArchivo()).isEqualTo("informe.pdf");
    }

    @Test
    void debeDelegarGenerarVersion_conElDomainMapeadoDelCommand() {
        // Arrange
        var interactor = new GenerarVersionArtefactoInteractorImpl(generarVersionUseCase);
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var command = GenerarVersionArtefactoCommand.crear(carga.toString(), artefacto, solicitante);
        var esperado = new VersionArtefactoGeneradaResult(UtilUUID.generarNuevoUUID(), 3);
        when(generarVersionUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(GeneracionVersionArtefactoDomain.class);
        verify(generarVersionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getCarga()).isEqualTo(carga);
        assertThat(captor.getValue().getArtefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().getSolicitante()).isEqualTo(solicitante);
    }
}
