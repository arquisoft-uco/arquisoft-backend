package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.mapper.FichaPerfilJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilCommandRepository fichaPerfilCommandRepository;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void registrarFicha(FichaPerfilEntity ficha) {
        fichaPerfilCommandRepository.save(FichaPerfilJpaMapper.toJpaEntity(ficha));
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), ficha.id());
    }

    @Override
    public void actualizarTitulo(UUID fichaPerfil, String tituloProyecto) {
        fichaPerfilCommandRepository.actualizarTitulo(fichaPerfil, tituloProyecto);
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), fichaPerfil);
    }

    @Override
    public void actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha) {
        fichaPerfilCommandRepository.actualizarAsesorFicha(
                fichaPerfil, AsesorFichaJpaMapper.toReferencia(nuevoAsesorFicha));
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), fichaPerfil);
    }

    @Override
    public Optional<FichaPerfilEntity> buscarPorId(UUID id) {
        return fichaPerfilCommandRepository.findById(id).map(FichaPerfilJpaMapper::toEntity);
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaPerfilCommandRepository.existsById(id);
    }

    @Override
    public boolean existePorTituloProyecto(String titulo) {
        return fichaPerfilCommandRepository.existsByTituloProyecto(titulo);
    }

    @Override
    public boolean existeTituloEnOtraFicha(UUID fichaPerfil, String titulo) {
        return fichaPerfilCommandRepository.existsByTituloProyectoAndIdNot(titulo, fichaPerfil);
    }
}
