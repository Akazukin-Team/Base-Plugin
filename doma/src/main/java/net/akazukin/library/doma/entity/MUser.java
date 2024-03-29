package net.akazukin.library.doma.entity;

import lombok.Getter;
import lombok.Setter;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "M_MAPART_LAND")
public class MUser {
    @Column(name = "LAND_ID")
    @Id
    private long landId;

    @Column(name = "NAME")
    private String name;
    @Column(name = "OWNER_UUID")
    private UUID ownerId;

    @Column(name = "CHUNK_X")
    private long chunkX;
    @Column(name = "CHUNK_Y")
    private long chunkY;

    @Column(name = "HEIGHT")
    private long height;
    @Column(name = "WIDHT")
    private long width;

    @Column(name = "CREATED_DATE")
    private Timestamp createDate;

    @Column(name = "VERSION_NO")
    @Version
    private long versionNo;
}
