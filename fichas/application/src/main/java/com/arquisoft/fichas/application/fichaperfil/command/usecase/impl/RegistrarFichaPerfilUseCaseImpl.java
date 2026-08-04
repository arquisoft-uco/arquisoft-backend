package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estudiantefichaperfil.aggregate.EstudianteFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estudiantefichaperfil.port.out.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;
    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;
    private final RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public UUID ejecutar(RegistrarFichaPerfilCommand entrada) {
        List<UUID> estudiantes = entrada.estudiantes();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.crear(
                entrada.tituloProyecto(),
                entrada.asesorFicha()
        );
        List<EstudianteFichaPerfilAggregate> relaciones =
                construirRelaciones(ficha.getId(), estudiantes);

        registrarFichaPerfilValidator.validar(ficha, estudiantes, relaciones);

        fichaPerfilOutputPort.guardar(ficha);
        asignarEstadoInicial(ficha.getId());
        relaciones.forEach(estudianteFichaPerfilOutputPort::guardar);

        logger.info(catalog.obtener(FichasKeys.FichaPerfil.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }

    private void asignarEstadoInicial(UUID fichaPerfil) {
        var estadoInicial = EstadoFichaPerfilAggregate.crear(fichaPerfil);
        estadoFichaPerfilOutputPort.guardar(estadoInicial);
        logger.info(catalog.obtener(FichasKeys.EstadoFichaPerfil.LOG_CREADO),
                estadoInicial.getId(),
                estadoInicial.getFichaPerfil(),
                estadoInicial.getEstadoFicha().getNombre());
    }

    private List<EstudianteFichaPerfilAggregate> construirRelaciones(
            UUID fichaPerfil, List<UUID> estudiantes) {
        if (UtilCollection.isEmptyOrNull(estudiantes)) {
            return List.of();
        }
        return EstudianteFichaPerfilAggregate.crear(fichaPerfil, estudiantes);
    }
}
