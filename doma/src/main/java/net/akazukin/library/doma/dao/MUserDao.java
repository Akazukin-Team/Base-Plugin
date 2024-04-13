package net.akazukin.library.doma.dao;

import net.akazukin.library.doma.entity.MUserEntity;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Script;
import org.seasar.doma.Select;
import org.seasar.doma.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface MUserDao {
    @Select
    List<MUserEntity> selectAll();

    @Select
    MUserEntity selectByPlayer(UUID player);

    @Insert
    int insert(MUserEntity entity);

    @Update
    int update(MUserEntity entity);

    @Delete
    int delete(MUserEntity entity);

    @Script
    void create();
}
