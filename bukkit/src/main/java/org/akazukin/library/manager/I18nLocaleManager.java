package org.akazukin.library.manager;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.akazukin.i18n.manager.II18nManager;
import org.akazukin.i18n.manager.data.II18nLang;
import org.akazukin.library.doma.LibrarySQLConfig;
import org.akazukin.library.doma.entity.MUserEntity;
import org.akazukin.library.doma.repo.MUserRepo;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class I18nLocaleManager {
    II18nManager i18nManager;

    public I18nLocaleManager(final II18nManager i18nManager) {
        this.i18nManager = i18nManager;
    }

    public II18nLang getByPlayer(final UUID player) {
        return LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
            final MUserEntity entity = MUserRepo.selectById(player);
            return entity == null || entity.getLocale() == null ? this.getConsole() : entity.getLocale();
        });
    }

    public II18nLang getConsole() {
        return this.i18nManager.getFormatter().getFallbackLang();
    }

    public void setByPlayer(final UUID player, final II18nLang i18nLang) {
        LibrarySQLConfig.singleton().getTransactionManager().required(() -> {
            MUserEntity entity = MUserRepo.selectById(player);
            if (entity == null) {
                entity = new MUserEntity();
                entity.setPlayerUuid(player);
            }
            entity.setLocale(i18nLang);
            MUserRepo.save(entity);
        });
    }
}
