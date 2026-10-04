package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.RemoverRevisionItemUseCase;
import com.arquisoft.fichas.domain.revisionitem.RemocionRevisionItemDomain;
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
class RemoverRevisionItemInteractorImplTest {

    @Mock
    private RemoverRevisionItemUseCase removerRevisionItemUseCase;

    @InjectMocks
    private RemoverRevisionItemInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var command = RemoverRevisionItemCommand.crear(revisionItem, asesorFicha);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(RemocionRevisionItemDomain.class);
        verify(removerRevisionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRevisionItem()).isEqualTo(revisionItem);
        assertThat(captor.getValue().getAsesorFicha()).isEqualTo(asesorFicha);
    }
}
