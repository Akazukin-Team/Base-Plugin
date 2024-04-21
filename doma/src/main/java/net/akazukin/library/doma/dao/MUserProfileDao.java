package net.akazukin.library.doma.dao;

import java.util.List;
import java.util.UUID;
import net.akazukin.library.doma.entity.MUserProfileEntity;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

@Dao
public interface MUserProfileDao {
    @Select
    List<MUserProfileEntity> selectAll();

    @Select
    MUserProfileEntity selectByPlayer(UUID player);

    @Insert
    int insert(MUserProfileEntity entity);

    @Update
    int update(MUserProfileEntity entity);

    @Delete
    int delete(MUserProfileEntity entity);

    @Script
    void create();
}
