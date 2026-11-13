package com.arquisoft.biblioteca.application.autor.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.autor.command.primaryport.model.RemoverAutorCommand;
import com.arquisoft.biblioteca.application.autor.command.result.RemocionAutorResult;
import com.arquisoft.biblioteca.application.autor.command.usecase.RemoverAutorUseCase;
import com.arquisoft.biblioteca.domain.autor.AutorDomain;
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
class RemoverAutorInteractorImplTest {

    @Mock
    private RemoverAutorUseCase removerAutorUseCase;

    @InjectMocks
    private RemoverAutorInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverAutorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new RemocionAutorResult.Removida(id);
        when(removerAutorUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        var captor = ArgumentCaptor.forClass(AutorDomain.class);
        verify(removerAutorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getIdentificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
