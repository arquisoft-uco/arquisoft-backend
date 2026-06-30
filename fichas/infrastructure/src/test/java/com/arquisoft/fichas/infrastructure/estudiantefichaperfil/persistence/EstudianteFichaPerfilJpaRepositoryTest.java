package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteFichaPerfilJpaRepositoryTest {

    @Autowired
    private EstudianteFichaPerfilJpaRepository repository;

    @Autowired
    private FichaPerfilJpaRepository fichaRepository;

    @Autowired
    private AsesorFichaJpaRepository asesorRepository;

    @Autowired
    private EstudianteJpaRepository estudianteRepository;

    @Test
    void debeRetornarFalse_cuandoRelacionNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        boolean resultado = repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);

        // Assert
        assertThat(resultado).isFalse();
    }

    private FichaPerfilJpaEntity crearFicha() {
        AsesorFichaJpaEntity asesor = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .nombre("Asesor de prueba")
                .email("asesor@example.com")
                .build();
        asesorRepository.save(asesor);

        FichaPerfilJpaEntity ficha = FichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Título de prueba")
                .asesorFicha(asesor)
                .build();
        return fichaRepository.save(ficha);
    }

    private EstudianteJpaEntity crearEstudiante() {
        EstudianteJpaEntity estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("1234567890")
                .nombre("Estudiante de prueba")
                .email("estudiante@example.com")
                .build();
        return estudianteRepository.save(estudiante);
    }
}
