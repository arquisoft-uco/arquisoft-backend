package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.application.administrador.command.usecase.RemoverAdministradorUseCase;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAdministradorInteractorImplTest {

    @Mock
    private RemoverAdministradorUseCase removerAdministradorUseCase;

    @InjectMocks
    private RemoverAdministradorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverAdministradorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new RemocionAdministradorResult.Removida(id);
        when(removerAdministradorUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(AdministradorDomain.class);
        verify(removerAdministradorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
