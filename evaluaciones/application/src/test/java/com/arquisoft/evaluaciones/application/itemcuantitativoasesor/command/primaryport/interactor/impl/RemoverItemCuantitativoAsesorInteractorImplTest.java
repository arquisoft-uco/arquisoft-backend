package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.usecase.RemoverItemCuantitativoAsesorUseCase;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.RemocionItemCuantitativoAsesorDomain;
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
class RemoverItemCuantitativoAsesorInteractorImplTest {

    @Mock
    private RemoverItemCuantitativoAsesorUseCase useCase;

    @InjectMocks
    private RemoverItemCuantitativoAsesorInteractorImpl interactor;

    @Test
    void debeMapearYDelegar_cuandoEjecutaCommand() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var command = RemoverItemCuantitativoAsesorCommand.crear(item);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionItemCuantitativoAsesorDomain.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getItemCuantitativoAsesor()).isEqualTo(item);
    }
}
