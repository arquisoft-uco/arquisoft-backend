package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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

    @Test
    void debeRetornarSoloElOtroEstudiante_cuandoLaFichaTieneDosVinculados() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-100", "Pedro Zapata", "pedro100@uco.edu.co");
        var companero = persistirEstudiante("EST-101", "Ana Ruiz", "ana101@uco.edu.co");
        vincular(ficha, solicitante);
        vincular(ficha, companero);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado =
                adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .singleElement()
                .satisfies(rm -> assertThat(rm.estudianteId()).isEqualTo(companero));
    }

    @Test
    void debeRetornarVacio_cuandoElSolicitanteEsElUnicoVinculado() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-110", "Ana Ruiz", "ana110@uco.edu.co");
        vincular(ficha, solicitante);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante)).isEmpty();
    }

    @Test
    void debeRetornarVacio_cuandoElSolicitanteNoEstaVinculadoAEsaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var otroEstudiante = persistirEstudiante("EST-120", "Ana Ruiz", "ana120@uco.edu.co");
        vincular(ficha, otroEstudiante);
        var solicitanteAjeno = persistirEstudiante("EST-121", "Pedro Zapata", "pedro121@uco.edu.co");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitanteAjeno)).isEmpty();
    }

    @Test
    void debeRetornarLosOtrosDosOrdenadosPorNombre_cuandoLaFichaTieneCupoMaximo() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-130", "Luis Paz", "luis130@uco.edu.co");
        var zapata = persistirEstudiante("EST-131", "Pedro Zapata", "pedro131@uco.edu.co");
        var ruiz = persistirEstudiante("EST-132", "Ana Ruiz", "ana132@uco.edu.co");
        vincular(ficha, solicitante);
        vincular(ficha, zapata);
        vincular(ficha, ruiz);
        entityManager.flush();

        // Act
        List<EstudianteFichaPerfilReadModel> resultado =
                adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::nombre)
                .containsExactly("Ana Ruiz", "Pedro Zapata");
    }

    @Test
    void debeIncluirAlEstudianteDadoDeBajaMarcadoNoVigente_cuandoConsultaElCoordinador() {
        // Arrange
        var ficha = UUID.randomUUID();
        var vigente = persistirEstudiante("EST-140", "Ana Ruiz", "ana140@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-141", "Pedro Zapata", "pedro141@uco.edu.co", Instant.now());
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId, EstudianteFichaPerfilReadModel::vigente)
                .containsExactly(tuple(vigente, true), tuple(dadoDeBaja, false));
    }

    @Test
    void debeRetornarSoloVigentes_cuandoSeConsultanLosVigentesDeLaFicha() {
        // Arrange
        var ficha = UUID.randomUUID();
        var vigente = persistirEstudiante("EST-160", "Ana Ruiz", "ana160@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-161", "Pedro Zapata", "pedro161@uco.edu.co", Instant.now());
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarVigentesPorFicha(ficha);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(vigente);
    }

    @Test
    void debeExcluirAlCompaneroDadoDeBaja_cuandoConsultaElEstudiante() {
        // Arrange
        var ficha = UUID.randomUUID();
        var solicitante = persistirEstudiante("EST-150", "Luis Paz", "luis150@uco.edu.co");
        var vigente = persistirEstudiante("EST-151", "Ana Ruiz", "ana151@uco.edu.co");
        var dadoDeBaja = persistirEstudiante("EST-152", "Pedro Zapata", "pedro152@uco.edu.co", Instant.now());
        vincular(ficha, solicitante);
        vincular(ficha, vigente);
        vincular(ficha, dadoDeBaja);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarCompanerosPorFichaYEstudiante(ficha, solicitante);

        // Assert
        assertThat(resultado)
                .extracting(EstudianteFichaPerfilReadModel::estudianteId)
                .containsExactly(vigente);
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email) {
        return persistirEstudiante(identificador, nombre, email, null);
    }

    private UUID persistirEstudiante(String identificador, String nombre, String email, Instant eliminadoEn) {
        var estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador(identificador)
                .nombre(nombre)
                .email(email)
                .ocurridoEn(Instant.now())
                .eliminadoEn(eliminadoEn)
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
