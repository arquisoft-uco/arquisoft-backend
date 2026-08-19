package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.TipoItemEnFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.AgregarItemFichaPerfilUseCase;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper.ItemFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilUseCaseImpl implements AgregarItemFichaPerfilUseCase {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;
    private final FichaPerfilExisteFinder fichaPerfilExisteFinder;
    private final VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;
    private final TipoItemEnFichaExisteFinder tipoItemEnFichaExisteFinder;
    private final AgregarItemFichaPerfilValidator agregarItemFichaPerfilValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionItemFichaPerfilDomain entrada) {
        var item = entrada.getItem();

        boolean fichaExiste = fichaPerfilExisteFinder.obtener(item.getFichaPerfilId());
        boolean esPropietario = vinculoEstudianteFichaExisteFinder.obtener(
                new VinculoEstudianteFicha(item.getFichaPerfilId(), entrada.getEstudiante()));
        boolean tipoYaExiste = tipoItemEnFichaExisteFinder.obtener(item);

        agregarItemFichaPerfilValidator.validar(
                item, entrada.getEstudiante(), fichaExiste, esPropietario, tipoYaExiste);

        itemFichaPerfilOutputPort.registrarItem(ItemFichaPerfilMapper.toEntity(item));

        logger.info(
                Mensajes.obtener(ItemFichaPerfilKey.LOG_AGREGADO),
                item.getId(),
                item.getFichaPerfilId(),
                item.getTipoItem()
        );

        return item.getId();
    }
}
