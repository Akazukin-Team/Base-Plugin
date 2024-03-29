package net.akazukin.library.doma.repo;

import net.akazukin.library.doma.SQLConfig;
import net.akazukin.library.doma.dao.MUserDao;
import net.akazukin.library.doma.dao.MUserDaoImpl;
import net.akazukin.library.doma.entity.MUser;

import java.util.List;
import java.util.UUID;

public class MMapartLandRepo {
    private static final MUserDao M_USER_DAO = new MUserDaoImpl(SQLConfig.singleton());

    public static MUser select(final UUID player) {
        return M_USER_DAO.selectByPlayer(player);
    }

    public static List<MUser> selectAll() {
        return M_USER_DAO.selectAll();
    }

    public static void save(final MUser entity) {
        if (entity.getVersionNo() < 0) {
            M_USER_DAO.insert(entity);
        } else {
            M_USER_DAO.update(entity);
        }
    }

    public static void delete(final MUser entity) {
        M_USER_DAO.delete(entity);
    }
}
