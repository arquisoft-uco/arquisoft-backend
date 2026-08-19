package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaExisteFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.usecase.AsignarEstadoInicialFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.mapper.FichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AsesorFichaExisteFinder asesorFichaExisteFinder;
    private final TituloFichaPerfilExisteFinder tituloFichaPerfilExisteFinder;
    private final RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    private final AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroFichaPerfilDomain registro) {
        var ficha = registro.getFicha();

        boolean asesorExiste = asesorFichaExisteFinder.obtener(ficha.getAsesorFicha());
        boolean tituloYaExiste = tituloFichaPerfilExisteFinder.obtener(ficha.getTituloProyecto());

        registrarFichaPerfilValidator.validar(ficha, asesorExiste, tituloYaExiste);

        fichaPerfilOutputPort.registrarFicha(FichaPerfilMapper.toEntity(ficha));

        logger.info(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());

        asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro);

        return ficha.getId();
    }
}
