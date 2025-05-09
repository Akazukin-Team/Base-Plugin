package org.akazukin.library.doma.repo;

import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.dao.MUserDao;
import org.akazukin.library.doma.dao.MUserDaoImpl;
import org.akazukin.library.doma.entity.MUserEntity;

import java.util.List;
import java.util.UUID;

public class MUserRepo {
    private static final MUserDao M_USER_DAO = new MUserDaoImpl(LibrarySQLConfig.singleton());

    public static MUserEntity selectById(final UUID player) {
        return M_USER_DAO.selectByPlayer(player);
    }

    public static List<MUserEntity> selectAll() {
        return M_USER_DAO.selectAll();
    }

    public static void save(final MUserEntity entity) {
        if (entity.getVersionNo() <= 0) {
            M_USER_DAO.insert(entity);
        } else {
            M_USER_DAO.update(entity);
        }
    }

    public static void delete(final MUserEntity entity) {
        M_USER_DAO.delete(entity);
    }
}
