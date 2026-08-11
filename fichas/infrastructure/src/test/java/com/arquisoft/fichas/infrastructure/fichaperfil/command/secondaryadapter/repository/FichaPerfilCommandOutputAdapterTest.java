package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilRepository;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FichaPerfilCommandOutputAdapterTest {

    @Mock
    private FichaPerfilRepository fichaPerfilRepository;

    @Mock
    private AsesorFichaRepository asesorFichaRepository;

    private FichaPerfilCommandOutputAdapter adapter;

    private UUID fichaId;
    private UUID asesorId;

    @BeforeEach
    void setUp() {
        fichaId = UUID.randomUUID();
        asesorId = UUID.randomUUID();
        adapter = new FichaPerfilCommandOutputAdapter(fichaPerfilRepository, asesorFichaRepository,
                org.mockito.Mockito.mock(com.arquisoft.shared.logger.AppLogger.class),
                CatalogoMensajesResourceBundle.porDefecto());
    }

    @Test
    void debeGuardarFichaPerfil_cuandoAgreggateEsValido() {
        // Arrange
        FichaPerfilDomain aggregate = FichaPerfilDomain.reconstruir(
                fichaId,
                "Proyecto de Prueba",
                asesorId
        );

        AsesorFichaEntity asesorRef = AsesorFichaEntity.builder()
                .id(asesorId)
                .build();

        when(asesorFichaRepository.getReferenceById(asesorId)).thenReturn(asesorRef);
        when(fichaPerfilRepository.save(any(FichaPerfilEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adapter.registrarFicha(aggregate);

        // Assert
        verify(asesorFichaRepository, times(1)).getReferenceById(asesorId);
        verify(fichaPerfilRepository, times(1)).save(any(FichaPerfilEntity.class));
    }

    @Test
    void debeBuscarPorId_cuandoIdExiste() {
        // Arrange
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(asesorId)
                .build();

        FichaPerfilEntity entity = FichaPerfilEntity.builder()
                .id(fichaId)
                .tituloProyecto("Proyecto Test")
                .asesorFicha(asesor)
                .build();

        when(fichaPerfilRepository.findById(fichaId)).thenReturn(Optional.of(entity));

        // Act
        Optional<FichaPerfilDomain> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).isPresent();
        verify(fichaPerfilRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeRetornarVacio_cuandoIdNoExiste() {
        // Arrange
        when(fichaPerfilRepository.findById(fichaId)).thenReturn(Optional.empty());

        // Act
        Optional<FichaPerfilDomain> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(fichaPerfilRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeRetornarTrue_cuandoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Unico";
        when(fichaPerfilRepository.existsByTituloProyecto(titulo)).thenReturn(true);

        // Act
        boolean existe = adapter.existePorTituloProyecto(titulo);

        // Assert
        assertThat(existe).isTrue();
        verify(fichaPerfilRepository, times(1)).existsByTituloProyecto(titulo);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Nuevo";
        when(fichaPerfilRepository.existsByTituloProyecto(titulo)).thenReturn(false);

        // Act
        boolean existe = adapter.existePorTituloProyecto(titulo);

        // Assert
        assertThat(existe).isFalse();
        verify(fichaPerfilRepository, times(1)).existsByTituloProyecto(titulo);
    }
}
