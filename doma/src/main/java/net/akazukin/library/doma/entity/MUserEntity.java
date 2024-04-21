package net.akazukin.library.doma.entity;

import java.util.UUID;
import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

@Entity
@Data
@Table(name = "M_USER")
public class MUserEntity {
    @Column(name = "PLAYER_UUID")
    @Id
    private UUID playerUuid;

    @Column(name = "LOCALE")
    private String locale;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo = -1;
}
