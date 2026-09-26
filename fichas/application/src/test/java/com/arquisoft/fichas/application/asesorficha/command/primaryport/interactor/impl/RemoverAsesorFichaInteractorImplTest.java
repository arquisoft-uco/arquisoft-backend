package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAsesorFichaInteractorImplTest {

    @Mock
    private RemoverAsesorFichaUseCase removerAsesorFichaUseCase;

    @InjectMocks
    private RemoverAsesorFichaInteractorImpl interactor;

    @Test
    void debeDelegarEnElUseCase_enRemoverAsesorFichaInteractor() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");
        var command = RemoverAsesorFichaCommand.crear(
                id.toString(), "1036950123", "Laura Gomez", "laura@uco.edu.co", ocurridoEn);
        var esperado = new RemocionAsesorFichaResult.Removida(id);
        when(removerAsesorFichaUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(command);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(AsesorFichaDomain.class);
        verify(removerAsesorFichaUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(id);
        assertThat(captor.getValue().getIdentificador()).isEqualTo("1036950123");
        assertThat(captor.getValue().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(captor.getValue().estaEliminado()).isFalse();
    }
}
