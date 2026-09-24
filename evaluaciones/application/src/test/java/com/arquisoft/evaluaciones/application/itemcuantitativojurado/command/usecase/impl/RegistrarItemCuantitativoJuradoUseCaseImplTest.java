package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.CategoriaItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.NombreItemCuantitativoJuradoPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.RegistrarItemCuantitativoJuradoValidator;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.CategoriaItemCuantitativoJuradoNoEncontradaException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarItemCuantitativoJuradoUseCaseImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @Mock
    private CategoriaItemCuantitativoJuradoExisteFinder categoriaFinder;

    @Mock
    private NombreItemCuantitativoJuradoPorCategoriaExisteFinder nombreFinder;

    @Mock
    private RegistrarItemCuantitativoJuradoValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarItemCuantitativoJuradoUseCaseImpl useCase;

    @Test
    void debePersistirYRetornarId_cuandoReglasSeCumplen() {
        // Arrange
        ItemCuantitativoJuradoDomain item = itemValido();
        when(categoriaFinder.obtener(item.getCategoria())).thenReturn(true);
        when(nombreFinder.obtener(item)).thenReturn(false);

        // Act
        UUID resultado = useCase.ejecutar(item);

        // Assert
        assertThat(resultado).isEqualTo(item.getId());
        verify(validator).validar(item, true, false);
        verify(outputPort).registrar(argThat(entity ->
                entity.id().equals(item.getId())
                        && entity.nombre().equals(item.getNombre())
                        && entity.categoriaId().equals(item.getCategoria())
                        && entity.valor().equals(item.getValor())));
        verify(logger).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void debeDetenerFlujo_cuandoCategoriaNoExiste() {
        // Arrange
        ItemCuantitativoJuradoDomain item = itemValido();
        when(categoriaFinder.obtener(item.getCategoria())).thenReturn(false);
        when(nombreFinder.obtener(item)).thenReturn(false);
        doThrow(new CategoriaItemCuantitativoJuradoNoEncontradaException(item.getCategoria()))
                .when(validator).validar(item, false, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item))
                .isInstanceOf(CategoriaItemCuantitativoJuradoNoEncontradaException.class);
        verify(outputPort, never()).registrar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    @Test
    void debePropagarError_cuandoPersistenciaFalla() {
        // Arrange
        ItemCuantitativoJuradoDomain item = itemValido();
        RuntimeException error = new RuntimeException("Error de persistencia");
        when(categoriaFinder.obtener(item.getCategoria())).thenReturn(true);
        when(nombreFinder.obtener(item)).thenReturn(false);
        doThrow(error).when(outputPort).registrar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(item)).isSameAs(error);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(item.getId()));
    }

    private static ItemCuantitativoJuradoDomain itemValido() {
        return ItemCuantitativoJuradoDomain.crear(
                "Calidad", "Descripción", UUID.randomUUID(), 100);
    }
}
