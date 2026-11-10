package py.edu.uc.lp3.persistence.repository;

import java.util.UUID;
import java.util.List;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import py.edu.uc.lp3.domain.Entidad;

public interface EntidadRepository extends JpaRepository<Entidad, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entidad from Entidad entidad where entidad.id in :ids order by entidad.id")
    List<Entidad> buscarParaExplosion(@Param("ids") List<UUID> ids);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update MobHostil mob set mob.objetivoActual = null where mob.objetivoActual.id = :id")
    void desvincularObjetivo(@Param("id") UUID id);
}
