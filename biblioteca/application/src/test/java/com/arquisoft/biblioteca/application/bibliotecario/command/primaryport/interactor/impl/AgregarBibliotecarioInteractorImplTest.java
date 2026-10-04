package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.biblioteca.application.bibliotecario.command.usecase.AgregarBibliotecarioUseCase;
import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
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
class AgregarBibliotecarioInteractorImplTest {

    @Mock
    private AgregarBibliotecarioUseCase agregarBibliotecarioUseCase;

    @InjectMocks
    private AgregarBibliotecarioInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_conElDomainMapeadoDelCommand() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = AgregarBibliotecarioCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);
        var resultadoEsperado = new AgregacionBibliotecarioResult.Agregada(id);
        when(agregarBibliotecarioUseCase.ejecutar(any())).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        var captor = ArgumentCaptor.forClass(BibliotecarioDomain.class);
        verify(agregarBibliotecarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getIdentificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
