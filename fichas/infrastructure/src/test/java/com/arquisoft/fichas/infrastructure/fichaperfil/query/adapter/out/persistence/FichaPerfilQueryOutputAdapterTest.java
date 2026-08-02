package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilRepository;
import com.arquisoft.shared.pagination.PaginatedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FichaPerfilQueryOutputAdapterTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FichaPerfilRepository fichaPerfilRepository;

    @Autowired
    private EstudianteFichaPerfilRepository estudianteFichaPerfilRepository;

    private FichaPerfilQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FichaPerfilQueryOutputAdapter(
                fichaPerfilRepository,
                new FichaPerfilJpaSpecification(),
                estudianteFichaPerfilRepository,
                org.mockito.Mockito.mock(com.arquisoft.shared.logger.AppLogger.class)
        );
    }

    @Test
    void debeRetornarReadModel_cuandoExistenEnBD() {
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-001")
                .nombre("Juan Salazar")
                .email("juan.salazar@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .tituloProyecto("Arquisoft Backend")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        entityManager.flush();

        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(
                FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1L);

        FichaPerfilReadModel fichaLeida = resultado.getContent().get(0);
        assertThat(fichaLeida.getTituloProyecto()).isEqualTo("Arquisoft Backend");
        assertThat(fichaLeida.getAsesorFicha().getNombre()).isEqualTo("Juan Salazar");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFichasEnBD() {
        PaginatedResult<FichaPerfilReadModel> resultado = adapter.consultarTodas(
                FichaPerfilCriteria.builder().pagina(0).tamanio(10).build());

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
    }

    @Test
    void debeRetornarTrue_cuandoEstudianteEsPropietario() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-001")
                .nombre("Juan Salazar")
                .email("juan.salazar@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(fichaPerfilId)
                .tituloProyecto("Arquisoft Backend")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);

        EstudianteFichaPerfilEntity relacion = EstudianteFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(estudianteId)
                .build();
        entityManager.persist(relacion);
        entityManager.flush();

        // Act
        boolean esPropietario = adapter.esEstudiantePropietario(new PropietarioFichaCriteria(fichaPerfilId, estudianteId));

        // Assert
        assertThat(esPropietario).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteNoEsPropietario() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        UUID otroEstudianteId = UUID.randomUUID();

        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-001")
                .nombre("Juan Salazar")
                .email("juan.salazar@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(fichaPerfilId)
                .tituloProyecto("Arquisoft Backend")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);

        EstudianteFichaPerfilEntity relacion = EstudianteFichaPerfilEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaPerfilId)
                .estudianteId(otroEstudianteId)
                .build();
        entityManager.persist(relacion);
        entityManager.flush();

        // Act
        boolean esPropietario = adapter.esEstudiantePropietario(new PropietarioFichaCriteria(fichaPerfilId, estudianteId));

        // Assert
        assertThat(esPropietario).isFalse();
    }

    @Test
    void debeRetornarTrue_cuandoFichaExistePorId() {
        // Arrange
        AsesorFichaEntity asesor = AsesorFichaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("DOC-002")
                .nombre("Ana Ramirez")
                .email("ana.ramirez@soyuco.edu.co")
                .build();
        entityManager.persist(asesor);

        UUID fichaId = UUID.randomUUID();
        FichaPerfilEntity ficha = FichaPerfilEntity.builder()
                .id(fichaId)
                .tituloProyecto("Proyecto Existente")
                .asesorFicha(asesor)
                .build();
        entityManager.persist(ficha);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorId(fichaId)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoFichaNoExistePorId() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }
}
