package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper.FichaPerfilMapper;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository.AsesorFichaCommandRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.mapper.FichaPerfilJpaMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FichaPerfilCommandRepositoryTest {

    @Autowired
    private FichaPerfilCommandRepository fichaPerfilRepository;

    @Autowired
    private AsesorFichaCommandRepository asesorFichaRepository;

    @Test
    void debeActualizarSoloElAsesor_cuandoLaFichaExiste() {
        // Arrange
        AsesorFichaJpaEntity asesorOriginal = asesorFichaRepository.saveAndFlush(asesor());
        AsesorFichaJpaEntity asesorNuevo = asesorFichaRepository.saveAndFlush(asesor());

        FichaPerfilJpaEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Proyecto de Prueba")
                .asesorFicha(asesorOriginal)
                .build());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarAsesorFicha(ficha.getId(), asesorNuevo);
        fichaPerfilRepository.flush();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        FichaPerfilJpaEntity actualizada = fichaPerfilRepository.findById(ficha.getId()).orElseThrow();
        assertThat(actualizada.getAsesorFicha().getId()).isEqualTo(asesorNuevo.getId());
        assertThat(actualizada.getTituloProyecto()).isEqualTo("Proyecto de Prueba");
    }

    @Test
    void debeRetornarCero_cuandoLaFichaNoExiste() {
        // Arrange
        AsesorFichaJpaEntity asesorNuevo = asesorFichaRepository.saveAndFlush(asesor());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarAsesorFicha(UUID.randomUUID(), asesorNuevo);

        // Assert
        assertThat(filasActualizadas).isEqualTo(0);
    }

    @Test
    void debeActualizarSoloElTitulo_cuandoLaFichaExiste() {
        // Arrange
        AsesorFichaJpaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        FichaPerfilJpaEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo original")
                .asesorFicha(asesor)
                .build());

        // Act
        int filasActualizadas = fichaPerfilRepository.actualizarTitulo(ficha.getId(), "Titulo nuevo");
        fichaPerfilRepository.flush();

        // Assert
        assertThat(filasActualizadas).isEqualTo(1);
        FichaPerfilJpaEntity actualizada = fichaPerfilRepository.findById(ficha.getId()).orElseThrow();
        assertThat(actualizada.getTituloProyecto()).isEqualTo("Titulo nuevo");
        assertThat(actualizada.getAsesorFicha().getId()).isEqualTo(asesor.getId());
    }

    @Test
    void debeDetectarTituloDuplicado_cuandoPerteneceAOtraFicha() {
        // Arrange
        AsesorFichaJpaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        fichaPerfilRepository.saveAndFlush(FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo compartido")
                .asesorFicha(asesor)
                .build());
        FichaPerfilJpaEntity otraFicha = fichaPerfilRepository.saveAndFlush(FichaPerfilJpaEntity.builder()
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
        AsesorFichaJpaEntity asesor = asesorFichaRepository.saveAndFlush(asesor());
        FichaPerfilJpaEntity ficha = fichaPerfilRepository.saveAndFlush(FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Titulo sin cambios")
                .asesorFicha(asesor)
                .build());

        // Act & Assert — el título es el mismo de la propia ficha, no de otra
        assertThat(fichaPerfilRepository.existsByTituloProyectoAndIdNot("Titulo sin cambios", ficha.getId()))
                .isFalse();
    }

    @Test
    void debeEscribirLaClaveForanea_cuandoElAsesorLlegaComoReferenciaSoloConId() {
        // Arrange — el caso de uso ya no puede pedir un proxy de Hibernate: el mapper construye
        // la asociacion solo con el id. Sin cascada, esa instancia separada basta para la FK.
        AsesorFichaJpaEntity asesorPersistido = asesorFichaRepository.saveAndFlush(asesor());
        FichaPerfilDomain dominio = FichaPerfilDomain.reconstruir(
                UUID.randomUUID(), "Proyecto mapeado", asesorPersistido.getId());

        // Act
        fichaPerfilRepository.saveAndFlush(
                FichaPerfilJpaMapper.toJpaEntity(FichaPerfilMapper.toEntity(dominio)));

        // Assert
        FichaPerfilJpaEntity guardada = fichaPerfilRepository.findById(dominio.getId()).orElseThrow();
        assertThat(guardada.getAsesorFicha().getId()).isEqualTo(asesorPersistido.getId());
        assertThat(guardada.getTituloProyecto()).isEqualTo("Proyecto mapeado");
    }

    private AsesorFichaJpaEntity asesor() {
        return AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(UUID.randomUUID().toString().substring(0, 20))
                .nombre("Ana Gomez")
                .email("ana.gomez@soyuco.edu.co")
                .build();
    }
}
