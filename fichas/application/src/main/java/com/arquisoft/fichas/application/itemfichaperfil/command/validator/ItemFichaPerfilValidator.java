package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilValidator {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    public void validarFichaPropia(UUID fichaPerfil, UUID estudiante) {
        if (!fichaPerfilQueryOutputPort.esEstudiantePropietario(
                new PropietarioFichaCriteria(fichaPerfil, estudiante))) {
            throw new ItemFichaNoPropiaException(fichaPerfil);
        }
    }

    public void validarTipoNoDuplicado(UUID fichaPerfil, String tipoItem) {
        if (itemFichaPerfilOutputPort.existePorFichaYTipoItem(fichaPerfil, tipoItem)) {
            throw new ItemTipoDuplicadoException(tipoItem);
        }
    }
}
