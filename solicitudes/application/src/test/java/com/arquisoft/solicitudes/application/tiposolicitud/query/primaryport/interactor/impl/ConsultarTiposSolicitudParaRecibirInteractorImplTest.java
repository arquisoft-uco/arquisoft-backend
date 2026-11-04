package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.solicitudes.application.tiposolicitud.query.criteria.TipoSolicitudParaRecibirCriteria;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudParaRecibirQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.application.tiposolicitud.query.usecase.ConsultarTiposSolicitudParaRecibirUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTiposSolicitudParaRecibirInteractorImplTest {

    @Mock
    private ConsultarTiposSolicitudParaRecibirUseCase useCase;

    @InjectMocks
    private ConsultarTiposSolicitudParaRecibirInteractorImpl interactor;

    @Test
    void debeDelegarEnUseCaseConActorYRolesDelQuery_yRetornarSuResultado() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var query = ConsultarTiposSolicitudParaRecibirQuery.crear(actor, List.of("COORDINADOR"));
        var resultadoEsperado = List.of(
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"));
        when(useCase.ejecutar(any(TipoSolicitudParaRecibirCriteria.class))).thenReturn(resultadoEsperado);

        // Act
        var resultado = interactor.ejecutar(query);

        // Assert
        var captor = ArgumentCaptor.forClass(TipoSolicitudParaRecibirCriteria.class);
        verify(useCase).ejecutar(captor.capture());
        assertThat(captor.getValue().actor()).isEqualTo(actor);
        assertThat(captor.getValue().rolesIdentidad()).containsExactly("COORDINADOR");
        assertThat(resultado).isSameAs(resultadoEsperado);
    }
}
