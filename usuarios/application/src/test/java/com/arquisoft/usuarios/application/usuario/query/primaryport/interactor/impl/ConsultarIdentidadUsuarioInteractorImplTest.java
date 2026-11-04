package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarIdentidadUsuarioQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarIdentidadUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarIdentidadUsuarioInteractorImplTest {

    @Mock
    private ConsultarIdentidadUsuarioUseCase consultarIdentidadUsuarioUseCase;

    @InjectMocks
    private ConsultarIdentidadUsuarioInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoRecibeElQuery() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var esperado = new IdentidadUsuarioReadModel("Ana María", "Ramírez Díaz");
        when(consultarIdentidadUsuarioUseCase.ejecutar(any(IdentidadUsuarioCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarIdentidadUsuarioQuery.crear(usuario));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(IdentidadUsuarioCriteria.class);
        verify(consultarIdentidadUsuarioUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().usuario()).isEqualTo(usuario);
    }
}
