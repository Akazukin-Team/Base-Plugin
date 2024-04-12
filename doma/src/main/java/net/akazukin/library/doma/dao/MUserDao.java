package net.akazukin.library.doma.dao;

import net.akazukin.library.doma.entity.MUser;
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
    List<MUser> selectAll();

    @Select
    MUser selectByPlayer(UUID player);

    @Insert
    int insert(MUser entity);

    @Update
    int update(MUser entity);

    @Delete
    int delete(MUser entity);

    @Script
    void create();
}
