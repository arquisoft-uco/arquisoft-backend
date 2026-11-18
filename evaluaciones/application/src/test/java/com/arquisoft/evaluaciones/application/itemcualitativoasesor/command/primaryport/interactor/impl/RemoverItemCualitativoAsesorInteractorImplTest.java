package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.usecase.RemoverItemCualitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.RemocionItemCualitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverItemCualitativoAsesorInteractorImplTest {

    @Mock
    private RemoverItemCualitativoAsesorUseCase useCase;

    @InjectMocks
    private RemoverItemCualitativoAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = RemoverItemCualitativoAsesorCommand.crear(item);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionItemCualitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCualitativoAsesor()).isEqualTo(item);
    }
}
