package com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.impl;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.usecase.ConsultarUsuariosAdministradorUseCase;
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
class ConsultarUsuariosAdministradorInteractorImplTest {

    @Mock
    private ConsultarUsuariosAdministradorUseCase consultarUsuariosAdministradorUseCase;

    @InjectMocks
    private ConsultarUsuariosAdministradorInteractorImpl interactor;

    @Test
    void debeDelegarCriteriaAlUseCase_cuandoRecibeQuery() {
        // Arrange
        var raiz = NodoFiltro.predicado("esEstudiante", FiltroOperador.ES, "true");
        var entrada = ConsultaCriteriaQuery.crear(1, 15, List.of(), raiz);
        var esperado = PaginatedResult.<UsuarioReadModel>of(List.of(), 1, 15, 0L);
        when(consultarUsuariosAdministradorUseCase.ejecutar(any())).thenReturn(esperado);

        // Act
        var resultado = interactor.ejecutar(entrada);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var captor = ArgumentCaptor.forClass(UsuarioCriteria.class);
        verify(consultarUsuariosAdministradorUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getPagina()).isEqualTo(1);
        assertThat(captor.getValue().getTamanio()).isEqualTo(15);
        assertThat(captor.getValue().getRaiz()).isEqualTo(raiz);
    }
}
