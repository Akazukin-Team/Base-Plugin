package org.akazukin.library.doma.repo;

import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.dao.MUserProfileDao;
import org.akazukin.library.doma.dao.MUserProfileDaoImpl;
import org.akazukin.library.doma.entity.MUserProfileEntity;

import java.util.List;
import java.util.UUID;

public class MUserProfileRepo {
    private static final MUserProfileDao M_USER_DAO = new MUserProfileDaoImpl(LibrarySQLConfig.singleton());

    public static MUserProfileEntity selectById(final UUID player) {
        return M_USER_DAO.selectByPlayer(player);
    }

    public static List<MUserProfileEntity> selectAll() {
        return M_USER_DAO.selectAll();
    }

    public static void save(final MUserProfileEntity entity) {
        if (entity.getVersionNo() <= 0) {
            M_USER_DAO.insert(entity);
        } else {
            M_USER_DAO.update(entity);
        }
    }

    public static void delete(final MUserProfileEntity entity) {
        M_USER_DAO.delete(entity);
    }
}
