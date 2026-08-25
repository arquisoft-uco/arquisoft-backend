package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloEnOtraFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.usecase.ModificarFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilUseCaseImpl implements ModificarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final TituloEnOtraFichaExisteFinder tituloEnOtraFichaExisteFinder;
    private final ModificarFichaPerfilValidator modificarFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionFichaPerfilDomain entrada) {
        boolean esPropietario = vinculoEstudianteFichaExisteFinder.obtener(
                new VinculoEstudianteFicha(entrada.getFichaPerfil(), entrada.getEstudiante()));
        boolean tituloYaExiste = tituloEnOtraFichaExisteFinder.obtener(entrada);

        modificarFichaPerfilValidator.validar(entrada, esPropietario, tituloYaExiste);

        fichaPerfilOutputPort.actualizarTitulo(entrada.getFichaPerfil(), entrada.getTituloProyecto());

        logger.info(Mensajes.obtener(FichaPerfilKey.LOG_MODIFICADA), entrada.getFichaPerfil());
    }
}
