package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaExisteFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * La asignación de estudiantes ya no se dispara aquí: la encadena
 * AsignarEstadoInicialFichaPerfilUseCase al terminar (ver
 * AsignarEstadoInicialFichaPerfilUseCaseTest).
 */
@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private AsesorFichaExisteFinder asesorFichaExisteFinder;

    @Mock
    private TituloFichaPerfilExisteFinder tituloFichaPerfilExisteFinder;

    @Mock
    private RegistrarFichaPerfilValidator registrarFichaPerfilValidator;

    @Mock
    private AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @InjectMocks
    private RegistrarFichaPerfilUseCaseImpl registrarFichaPerfilUseCase;

    @Test
    void debeRegistrar_cuandoDatosValidos() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), true, false);

        // Act
        UUID resultado = registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isEqualTo(registro.getFichaPerfil());
        verify(fichaPerfilOutputPort, times(1)).registrarFicha(entidadDe(registro.getFicha()));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, true, false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        InOrder inOrder = inOrder(asesorFichaExisteFinder, tituloFichaPerfilExisteFinder,
                registrarFichaPerfilValidator, fichaPerfilOutputPort);
        inOrder.verify(asesorFichaExisteFinder).obtener(ficha.getAsesorFicha());
        inOrder.verify(tituloFichaPerfilExisteFinder).obtener(ficha.getTituloProyecto());
        inOrder.verify(registrarFichaPerfilValidator).validar(ficha, true, false);
        inOrder.verify(fichaPerfilOutputPort).registrarFicha(entidadDe(ficha));
    }

    @Test
    void debeEncadenarElEstadoInicialConElMismoRegistro_despuesDePersistirLaFicha() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), true, false);

        // Act
        registrarFichaPerfilUseCase.ejecutar(registro);

        // Assert
        verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(registro);

        InOrder inOrder = inOrder(fichaPerfilOutputPort, asignarEstadoInicialFichaPerfilUseCase);
        inOrder.verify(fichaPerfilOutputPort).registrarFicha(any());
        inOrder.verify(asignarEstadoInicialFichaPerfilUseCase).ejecutar(any());
    }

    @Test
    void debePasarElResultadoDeLasConsultasAlValidator_cuandoElAsesorNoExiste() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, false, false);
        doThrow(new AsesorFichaNoEncontradoException(ficha.getAsesorFicha()))
                .when(registrarFichaPerfilValidator).validar(ficha, false, false);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
        verify(asignarEstadoInicialFichaPerfilUseCase, never()).ejecutar(any());
    }

    @Test
    void debePasarElResultadoDeLasConsultasAlValidator_cuandoElTituloEstaDuplicado() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        FichaPerfilDomain ficha = registro.getFicha();
        stubConsultas(ficha, true, true);
        doThrow(new FichaTituloDuplicadoException(ficha.getTituloProyecto()))
                .when(registrarFichaPerfilValidator).validar(ficha, true, true);

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).registrarFicha(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        RegistroFichaPerfilDomain registro = registroValido();
        stubConsultas(registro.getFicha(), true, false);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort).registrarFicha(entidadDe(registro.getFicha()));

        // Act & Assert
        assertThatThrownBy(() -> registrarFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(InfrastructureException.class);

        verify(asignarEstadoInicialFichaPerfilUseCase, never()).ejecutar(any());
    }

    private void stubConsultas(FichaPerfilDomain ficha, boolean asesorExiste, boolean tituloYaExiste) {
        when(asesorFichaExisteFinder.obtener(ficha.getAsesorFicha())).thenReturn(asesorExiste);
        when(tituloFichaPerfilExisteFinder.obtener(ficha.getTituloProyecto())).thenReturn(tituloYaExiste);
    }

    private static RegistroFichaPerfilDomain registroValido() {
        var ficha = FichaPerfilDomain.crear("Título de prueba", UUID.randomUUID());

        return RegistroFichaPerfilDomain.crear(
                ficha,
                EstadoFichaPerfilDomain.crear(ficha.getId()),
                AgregacionEstudiantesFichaPerfilDomain.crear(
                        EstudianteFichaPerfilDomain.crear(ficha.getId(), List.of(UUID.randomUUID()))));
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static FichaPerfilEntity entidadDe(FichaPerfilDomain ficha) {
        return argThat(entity -> entity.id().equals(ficha.getId())
                && entity.tituloProyecto().equals(ficha.getTituloProyecto())
                && entity.asesorFicha().equals(ficha.getAsesorFicha()));
    }
}
