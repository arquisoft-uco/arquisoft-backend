package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarUsuariosAdministradorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.UsuarioQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarUsuariosAdministradorUseCaseImplTest {

    @Mock
    private UsuarioQueryOutputPort usuarioQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarUsuariosAdministradorUseCaseImpl useCase;

    @Test
    void debeRetornarPaginaDelPuerto_cuandoHayUsuarios() {
        // Arrange
        var criteria = UsuarioCriteria.builder().pagina(0).tamanio(10).build();
        var estudianteAsesor = new UsuarioReadModel(UtilUUID.generarNuevoUUID(), "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "ACTIVO", true, true, true, false, false, false, false, false);
        var eliminado = new UsuarioReadModel(UtilUUID.generarNuevoUUID(), "1002", "Bruno Diaz",
                "bruno.diaz@uco.edu.co", "3000000001", "INACTIVO", false, false, false, false, false, false, false, false);
        var esperado = PaginatedResult.of(List.of(estudianteAsesor, eliminado), 0, 10, 2L);
        when(usuarioQueryOutputPort.consultarTodos(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(usuarioQueryOutputPort, times(1)).consultarTodos(criteria);
        verify(logger).debug(eq(ConsultarUsuariosAdministradorKey.LOG_CONSULTANDO),
                eq(0), eq(10), eq(false), eq(false));
        verify(logger).debug(eq(ConsultarUsuariosAdministradorKey.LOG_CONSULTA_COMPLETADA),
                eq(2L), eq(0), eq(10));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayCoincidencias() {
        // Arrange
        var criteria = UsuarioCriteria.builder().pagina(0).tamanio(10).build();
        when(usuarioQueryOutputPort.consultarTodos(criteria)).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
