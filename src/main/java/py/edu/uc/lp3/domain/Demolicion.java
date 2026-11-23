package py.edu.uc.lp3.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "demoliciones")
public class Demolicion {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID explosivoNicoId;
    @Column(nullable = false)
    private Long creeperAmirId;
    @Column(nullable = false)
    private Instant fecha;
    @Lob
    @Column(nullable = false)
    private String resultadoAmir;

    protected Demolicion() {
    }

    public Demolicion(UUID explosivoNicoId, Long creeperAmirId, String resultadoAmir) {
        this.id = UUID.randomUUID();
        this.explosivoNicoId = explosivoNicoId;
        this.creeperAmirId = creeperAmirId;
        this.fecha = Instant.now();
        this.resultadoAmir = resultadoAmir;
    }

    public UUID getId() { return id; }
    public UUID getExplosivoNicoId() { return explosivoNicoId; }
    public Long getCreeperAmirId() { return creeperAmirId; }
    public Instant getFecha() { return fecha; }
    public String getResultadoAmir() { return resultadoAmir; }
}
