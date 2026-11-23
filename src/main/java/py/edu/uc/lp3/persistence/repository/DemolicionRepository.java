package py.edu.uc.lp3.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Demolicion;

public interface DemolicionRepository extends JpaRepository<Demolicion, UUID> {
    List<Demolicion> findAllByOrderByFechaDesc();
}
