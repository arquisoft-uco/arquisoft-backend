package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilValidator {

    private final AsesorFichaQueryOutputPort asesorFichaQueryOutputPort;
    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final FichaPerfilExisteRule fichaPerfilExisteRule;

    public void validarAsesorExiste(UUID asesorFicha) {
        if (!asesorFichaQueryOutputPort.existePorId(asesorFicha)) {
            throw new AsesorFichaNoEncontradoException(asesorFicha);
        }
    }

    public void validarFichaExiste(UUID fichaPerfil) {
        fichaPerfilExisteRule.validar(fichaPerfil);
    }

    public void validarTituloUnico(String tituloProyecto) {
        if (fichaPerfilOutputPort.existePorTituloProyecto(tituloProyecto)) {
            throw new FichaTituloDuplicadoException(tituloProyecto);
        }
    }

    public void validarEstudiantePropietario(PropietarioFichaCriteria criteria) {
        if (!fichaPerfilQueryOutputPort.esEstudiantePropietario(criteria)) {
            throw new FichaNoPropietarioException(criteria.fichaPerfil(), criteria.estudiante());
        }
    }
}
