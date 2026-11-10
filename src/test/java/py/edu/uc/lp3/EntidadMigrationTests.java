package py.edu.uc.lp3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.persistence.repository.EntidadRepository;
import py.edu.uc.lp3.service.EntidadService;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:migracion-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/legacy-entidades.sql,classpath:db/upgrade-entidades.sql"
})
class EntidadMigrationTests {
    @Autowired private EntidadRepository repository;
    @Autowired private EntidadService service;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void actualizaElEsquemaAnteriorSinPerderDatosYPermiteReiniciar() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        assertEquals(8, repository.count());
        assertInstanceOf(Zombie.class, repository.findById(id).orElseThrow());
        var zombie = service.obtener(id);
        assertEquals("Anterior", zombie.nombre());
        assertEquals(25, zombie.salud());
        assertEquals(1, zombie.ubicacion().getX());
        assertEquals(25.0, zombie.atributos().get("saludMaxima"));
        assertEquals(1.5, zombie.atributos().get("velocidad"));
        assertEquals(8, service.listar().size());

        jdbc.update("update entidades set velocidad = 4 where id = ?", id);
        jdbc.execute((ConnectionCallback<Void>) connection -> {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/upgrade-entidades.sql"));
            return null;
        });
        assertEquals(4.0, service.obtener(id).atributos().get("velocidad"));
        assertEquals(8, repository.count());
    }
}
