package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioPorCorreoCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarUsuarioPorCorreoQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuarioPorCorreoUseCase;
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
class ConsultarUsuarioPorCorreoInteractorImplTest {

    @Mock
    private ConsultarUsuarioPorCorreoUseCase consultarUsuarioPorCorreoUseCase;

    @InjectMocks
    private ConsultarUsuarioPorCorreoInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoRecibeElQuery() {
        // Arrange
        var esperado = new UsuarioPorCorreoReadModel(UtilUUID.generarNuevoUUID());
        when(consultarUsuarioPorCorreoUseCase.ejecutar(any(UsuarioPorCorreoCriteria.class))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarUsuarioPorCorreoQuery.crear("  ana@uco.edu.co "));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(UsuarioPorCorreoCriteria.class);
        verify(consultarUsuarioPorCorreoUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().correo()).isEqualTo("ana@uco.edu.co");
    }
}
