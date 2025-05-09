package org.akazukin.library.doma.entity;

import lombok.Data;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.util.UUID;

@Entity
@Data
@Table(name = "M_USER_PROFILE")
public class MUserProfileEntity {
    @Column(name = "PLAYER_UUID")
    @Id
    private UUID playerUuid;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SKIN")
    private String skin;

    @Column(name = "SKIN_MODEL")
    private String skinModel;

    @Column(name = "CAPE")
    private String cape;

    @Column(name = "TIMESTAMP")
    private long timestamp;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo = -1;
}
