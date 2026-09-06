package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteFichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteFichaPerfilQueryRepository repository;

    private EstudianteFichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteFichaPerfilQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarEstudiantesOrdenadosPorNombre_cuandoLaFichaTieneVinculos() {
        // Arrange
        var ficha = UUID.randomUUID();
        var pedro = persistirEstudiante("EST-001", "Pedro Zapata", "pedro@uco.edu.co");
        var ana = persistirEstudiante("EST-002", "Ana Ruiz", "ana@uco.edu.co");
        vincular(ficha, pedro);
        vincular(ficha, ana);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::nombre)
                .containsExactly("Ana Ruiz", "Pedro Zapata");
    }

    @Test
    void debeMapearNombreYEmailDesdeLaTablaEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = persistirEstudiante("EST-010", "Ana Ruiz", "ana.ruiz@uco.edu.co");
        var vinculoId = vincular(ficha, estudiante);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado).singleElement().satisfies(rm -> {
            assertThat(rm.id()).isEqualTo(vinculoId);
            assertThat(rm.fichaPerfilId()).isEqualTo(ficha);
            assertThat(rm.estudianteId()).isEqualTo(estudiante);
            assertThat(rm.nombre()).isEqualTo("Ana Ruiz");
            assertThat(rm.email()).isEqualTo("ana.ruiz@uco.edu.co");
        });
    }

    @Test
    void debeFiltrarSoloLosVinculosDeLaFichaSolicitada() {
        // Arrange
        var fichaA = UUID.randomUUID();
        var fichaB = UUID.randomUUID();
        var e1 = persistirEstudiante("EST-020", "Ana Ruiz", "ana20@uco.edu.co");
        var e2 = persistirEstudiante("EST-021", "Luis Paz", "luis21@uco.edu.co");
        vincular(fichaA, e1);
        vincular(fichaB, e2);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado = adapter.consultarPorFicha(fichaA);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(e1);
    }

    @Test
    void debeRetornarVacio_cuandoLaFichaNoTieneVinculos() {
        // Arrange
        var ficha = UUID.randomUUID();
        var estudiante = persistirEstudiante("EST-030", "Ana Ruiz", "ana30@uco.edu.co");
        vincular(UUID.randomUUID(), estudiante);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarPorFicha(ficha)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoLaFichaNoExiste() {
        // Act & Assert
        assertThat(adapter.consultarPorFicha(UUID.randomUUID())).isEmpty();
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email) {
        var estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .build();
        entityManager.persist(estudiante);
        return estudiante.getId();
    }

    private UUID vincular(UUID fichaPerfilId, UUID estudianteId) {
        var vinculo = EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build();
        entityManager.persist(vinculo);
        return vinculo.getId();
    }
}
