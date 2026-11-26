package com.arquisoft.shared.catalogoparametros.application.command.usecase.impl;

import com.arquisoft.shared.catalogoparametros.application.command.result.RefrescoParametrosCatalogoResult;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.FuenteParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model.ObtencionParametrosCatalogo;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefrescarParametrosCatalogoUseCaseImplTest {

    @Mock
    private FuenteParametrosCatalogoOutputPort fuente;
    @Mock
    private ParametrosCatalogoOutputPort almacen;
    @Mock
    private AppLogger logger;
    @InjectMocks
    private RefrescarParametrosCatalogoUseCaseImpl useCase;

    @Test
    void debeReemplazarCopia_cuandoObtenida() {
        // Arrange
        var entity = new ParametrosCatalogoEntity(Map.of("A", "1", "B", "2"));
        when(almacen.suspendido()).thenReturn(false);
        when(fuente.obtener()).thenReturn(new ObtencionParametrosCatalogo.Obtenida(entity));

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new RefrescoParametrosCatalogoResult.Refrescado(2));
        verify(almacen).reemplazar(entity);
    }

    @Test
    void debeConservarCopia_cuandoNoDisponible() {
        // Arrange
        when(almacen.suspendido()).thenReturn(false);
        when(fuente.obtener()).thenReturn(new ObtencionParametrosCatalogo.NoDisponible("estado-503"));

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new RefrescoParametrosCatalogoResult.Conservado("estado-503"));
        verify(almacen, never()).reemplazar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void debeSuspender_cuandoRechazada() {
        // Arrange
        when(almacen.suspendido()).thenReturn(false);
        when(fuente.obtener()).thenReturn(new ObtencionParametrosCatalogo.Rechazada(401));

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new RefrescoParametrosCatalogoResult.Rechazado(401));
        verify(almacen).suspender();
    }

    @Test
    void debeOmitirLlamada_cuandoSuspendido() {
        // Arrange
        when(almacen.suspendido()).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new RefrescoParametrosCatalogoResult.Suspendido());
        verifyNoInteractions(fuente);
    }

    @Test
    void debeRetornarSinConfigurar_cuandoNoHayConfiguracion() {
        // Arrange
        when(almacen.suspendido()).thenReturn(false);
        when(fuente.obtener()).thenReturn(new ObtencionParametrosCatalogo.SinConfigurar());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEqualTo(new RefrescoParametrosCatalogoResult.SinConfigurar());
    }
}
