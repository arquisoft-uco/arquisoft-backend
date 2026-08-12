package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.RegistrarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
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
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(FichaPerfilDomain ficha) {
        boolean asesorExiste = asesorFichaExisteFinder.obtener(ficha.getAsesorFicha());
        boolean tituloYaExiste = tituloFichaPerfilExisteFinder.obtener(ficha.getTituloProyecto());

        registrarFichaPerfilValidator.validar(ficha, asesorExiste, tituloYaExiste);

        fichaPerfilOutputPort.registrarFicha(FichaPerfilMapper.toEntity(ficha));

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }
}
