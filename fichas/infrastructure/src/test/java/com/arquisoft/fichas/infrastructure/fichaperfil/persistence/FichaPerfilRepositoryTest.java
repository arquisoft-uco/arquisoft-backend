package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FichaPerfilRepositoryTest {

    @Autowired
    private FichaPerfilRepository fichaPerfilRepository;

    @Autowired
    private AsesorFichaRepository asesorFichaRepository;

    @Test
    void debeActualizarSoloElAsesor_cuandoLaFichaExiste() {
        // Arrange
        AsesorFichaEntity asesorOriginal = asesorFichaRepository.saveAndFlush(asesor());
        AsesorFichaEntity asesorNuevo = asesorFichaRepository.saveAndFlush(asesor());

        FichaPerfilEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Proyecto de Prueba")
                .asesorFicha(asesorOriginal)
                .build());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarAsesorFicha(ficha.getId(), asesorNuevo);
        fichaPerfilRepository.flush();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        FichaPerfilEntity actualizada = fichaPerfilRepository.findById(ficha.getId()).orElseThrow();
        assertThat(actualizada.getAsesorFicha().getId()).isEqualTo(asesorNuevo.getId());
        assertThat(actualizada.getTituloProyecto()).isEqualTo("Proyecto de Prueba");
    }

    @Test
    void debeRetornarCero_cuandoLaFichaNoExiste() {
        // Arrange
        AsesorFichaEntity asesorNuevo = asesorFichaRepository.saveAndFlush(asesor());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarAsesorFicha(UUID.randomUUID(), asesorNuevo);

        // Assert
        assertThat(filasActualizadas).isEqualTo(0);
    }

    @Test
    void debeActualizarSoloElTitulo_cuandoLaFichaExiste() {
        // Arrange
        AsesorFichaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        FichaPerfilEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo original")
                .asesorFicha(asesor)
                .build());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarTitulo(ficha.getId(), "Titulo nuevo");
        fichaPerfilRepository.flush();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        FichaPerfilEntity actualizada = fichaPerfilRepository.findById(ficha.getId()).orElseThrow();
        assertThat(actualizada.getTituloProyecto()).isEqualTo("Titulo nuevo");
        assertThat(actualizada.getAsesorFicha().getId()).isEqualTo(asesor.getId());
    }

    @Test
    void debeDetectarTituloDuplicado_cuandoPerteneceAOtraFicha() {
        // Arrange
        AsesorFichaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        fichaPerfilRepository.saveAndFlush(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo compartido")
                .asesorFicha(asesor)
                .build());
        FichaPerfilEntity otraFicha = fichaPerfilRepository.saveAndFlush(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo propio")
                .asesorFicha(asesor)
                .build());

        // Act & Assert
        assertThat(fichaPerfilRepository.existsByTituloProyectoAndIdNot("Titulo compartido", otraFicha.getId()))
                .isTrue();
    }

    @Test
    void debePermitirElPropioTitulo_cuandoNoLoTieneOtraFicha() {
        // Arrange
        AsesorFichaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        FichaPerfilEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo sin cambios")
                .asesorFicha(asesor)
                .build());

        // Act & Assert — el título es el mismo de la propia ficha, no de otra
        assertThat(fichaPerfilRepository.existsByTituloProyectoAndIdNot("Titulo sin cambios", ficha.getId()))
                .isFalse();
    }

    private AsesorFichaEntity asesor() {
        return AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(UUID.randomUUID().toString().substring(0, 20))
                .nombre("Ana Gomez")
                .email("ana.gomez@soyuco.edu.co")
                .build();
    }
}
