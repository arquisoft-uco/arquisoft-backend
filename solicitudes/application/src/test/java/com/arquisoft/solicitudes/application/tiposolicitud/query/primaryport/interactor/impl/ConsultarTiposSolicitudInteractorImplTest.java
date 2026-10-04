package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudInteractorImplTest {

    @Mock
    private ConsultarTiposSolicitudUseCase consultarTiposSolicitudUseCase;

    @InjectMocks
    private ConsultarTiposSolicitudInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCase_yRetornarSuResultado() {
        // Arrange
        var resultadoEsperado = List.of(
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"));
        when(consultarTiposSolicitudUseCase.ejecutar()).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar();

        // Assert
        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(consultarTiposSolicitudUseCase).ejecutar();
    }
}
