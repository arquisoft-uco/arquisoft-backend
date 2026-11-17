package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.EstadoPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarMiPerfilUseCase;
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
class ConsultarMiPerfilInteractorImplTest {

    @Mock
    private ConsultarMiPerfilUseCase consultarMiPerfilUseCase;

    @InjectMocks
    private ConsultarMiPerfilInteractorImpl interactor;

    @Test
    void debeConvertirYDelegarAlUseCase_cuandoRecibeElQuery() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var esperado = new PerfilUsuarioReadModel(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", new EstadoPerfilReadModel("ACTIVO", "Activo"), List.of());
        when(consultarMiPerfilUseCase.ejecutar(new PerfilUsuarioCriteria(usuario))).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(ConsultarMiPerfilQuery.crear(usuario));

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(consultarMiPerfilUseCase).ejecutar(new PerfilUsuarioCriteria(usuario));
    }
}
