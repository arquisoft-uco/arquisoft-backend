package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.mapper.ItemCualitativoJuradoJpaMapper;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemCualitativoJuradoCommandOutputAdapter
        implements ItemCualitativoJuradoOutputPort {

    private final ItemCualitativoJuradoCommandRepository repository;

    @Override
    public void registrar(ItemCualitativoJuradoEntity entity) {
        try {
            repository.saveAndFlush(ItemCualitativoJuradoJpaMapper.toJpaEntity(entity));
        } catch (DataIntegrityViolationException exception) {
            throw new NombreItemCualitativoJuradoDuplicadoException(entity.nombre());
        } catch (DataAccessException exception) {
            throw errorPersistencia(exception);
        }
    }

    @Override
    public Boolean existePorNombreIgnorandoMayusculas(String nombre) {
        try {
            return repository.existsByNombreIgnoreCase(nombre);
        } catch (DataAccessException exception) {
            throw errorPersistencia(exception);
        }
    }

    private InfrastructureException errorPersistencia(DataAccessException exception) {
        return new InfrastructureException(
                Mensajes.obtener(ItemCualitativoJuradoKey.ERROR_PERSISTENCIA),
                EvaluacionesCodes.ItemCualitativoJurado.PERSISTENCIA_ERROR,
                exception);
    }
}
