package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstudiantesFichaPerfilUseCaseImplTest {

    @Mock
    private EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstudiantesFichaPerfilUseCaseImpl useCase;

    @Test
    void debeRetornarLista_cuandoLaFichaTieneEstudiantes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var criteria = new EstudianteFichaPerfilCriteria(fichaPerfil);
        var esperado = List.of(new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaPerfil, UUID.randomUUID(), "Ana Ruiz", "ana.ruiz@uco.edu.co"));
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(fichaPerfil)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estudianteFichaPerfilQueryOutputPort).consultarPorFicha(fichaPerfil);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstudiantes() {
        // Arrange
        var criteria = new EstudianteFichaPerfilCriteria(UUID.randomUUID());
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = new EstudianteFichaPerfilCriteria(UUID.randomUUID());
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(any())).thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(EstudianteFichaPerfilKey.LOG_CONSULTANDO), eq(criteria.fichaPerfil()));
        verify(logger).debug(eq(EstudianteFichaPerfilKey.LOG_CONSULTA_COMPLETADA), eq(0));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
