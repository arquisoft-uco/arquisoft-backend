package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.EnviarCorreccionesRevisionItemUseCase;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
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
class EnviarCorreccionesRevisionItemInteractorImplTest {

    @Mock
    private EnviarCorreccionesRevisionItemUseCase enviarCorreccionesRevisionItemUseCase;

    @InjectMocks
    private EnviarCorreccionesRevisionItemInteractorImpl interactor;

    @Test
    void debeMapearYDelegarEnElUseCase_cuandoEjecuta() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = EnviarCorreccionesRevisionItemCommand.crear(revisionItem, estudiante);

        // Act
        interactor.ejecutar(command);

        // Assert
        var captor = ArgumentCaptor.forClass(EnvioCorreccionesRevisionItemDomain.class);
        verify(enviarCorreccionesRevisionItemUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getRevisionItem()).isEqualTo(revisionItem);
        assertThat(captor.getValue().getEstudiante()).isEqualTo(estudiante);
    }
}
