/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.connection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.Entity;
import net.dirtcraft.dirtcore.common.storage.entities.VerificationEntity;
import net.dirtcraft.dirtcore.common.storage.entities.VoteDataEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.ChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.UnlockedChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.UnlockedPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateKeyItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateLocationEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentCommandEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.economy.WorthItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitClaimEntryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitEntity;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockEntity;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockEntryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockRuleEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.player.PlayerDataEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.KickEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnbanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnmuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.WarnEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RevertingPunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.BanHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.MuteHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.ExpirablePunishmentHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.PunishmentHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictedEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictionActionEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictionWorldEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.item.RestrictedItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.item.RestrictionAlternativeEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.mod.RestrictedModEntity;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserEntity;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserIPHistory;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserSettingsEntity;
import net.dirtcraft.dirtcore.common.storage.entities.util.ItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.util.ItemStackEntity;
import net.dirtcraft.storageutils.StorageCredentials;
import net.dirtcraft.storageutils.StorageType;
import net.dirtcraft.storageutils.hibernate.connection.AbstractHibernateConnectionFactory;
import net.dirtcraft.storageutils.logging.LoggerAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.cfg.Configuration;

public class DirtCoreHibernateConnectionFactory extends AbstractHibernateConnectionFactory {

    public static final Set<Class<?>> ENTITY_CLASSES =
            ImmutableSet.of(ChatMarkerEntity.class, BanEntity.class, BanHistoryEntity.class,
                    CrateContentCommandEntity.class, CrateContentEntity.class,
                    CrateContentItemEntity.class, CrateEntity.class, CrateKeyItemEntity.class,
                    CrateLocationEntity.class, ExpirablePunishmentEntity.class,
                    ExpirablePunishmentHistoryEntity.class, ItemEntity.class, ItemStackEntity.class,
                    KickEntity.class, KitClaimEntryEntity.class, KitEntity.class,
                    KitItemEntity.class, LimitedBlockEntity.class, LimitedBlockEntryEntity.class,
                    LimitedBlockRuleEntity.class, LogEntity.class, MuteEntity.class,
                    MuteHistoryEntity.class, PlayerDataEntity.class, PrefixEntity.class,
                    PunishmentEntity.class, PunishmentHistoryEntity.class, RestrictedEntity.class,
                    RestrictedItemEntity.class, RestrictedModEntity.class,
                    RestrictionActionEntity.class, RestrictionAlternativeEntity.class,
                    RestrictionWorldEntity.class, RevertingPunishmentEntity.class,
                    StaffPrefixEntity.class, UnbanEntity.class, UnlockedChatMarkerEntity.class,
                    UnlockedPrefixEntity.class, UnmuteEntity.class, UserEntity.class,
                    UserIPHistory.class, UserSettingsEntity.class, VerificationEntity.class,
                    VoteDataEntity.class, WarnEntity.class, WorthItemEntity.class);

    public DirtCoreHibernateConnectionFactory(final LoggerAdapter logger,
            final StorageType storageType, final StorageCredentials credentials) {
        super(logger, storageType, credentials);
    }

    @Override
    protected void addAnnotatedClasses(@NonNull final Configuration configuration) {
        for (final Class<?> entityClass : ENTITY_CLASSES) {
            if (entityClass.isAnnotationPresent(Entity.class)) {
                configuration.addAnnotatedClass(entityClass);
            } else {
                this.logger.warn("Could not add '{}' as an annotated class.",
                        entityClass.getName());
            }
        }
    }

    @Override
    protected void addProperties(@NonNull final Configuration configuration) {
        final Map<String, String> properties = ImmutableMap.<String, String>builder()
                .put("hibernate.connection.autoReconnect", "true")
                .put("hibernate.connection.autoReconnectForPools", "true")
                .put("hibernate.connection.connection.is-connection-validation-required", "true")
                .put("hibernate.connection.driver_class", this.getDriverClass())
                .put("hibernate.connection.url", this.getUrl())
                .put("hibernate.connection.username", this.getUsername())
                .put("hibernate.connection.password", this.getPassword())
                .put("hibernate.connection.pool_size", String.valueOf(this.getPoolSize()))
                .put("hibernate.show_sql", "false")
                .put("hibernate.dialect", this.getHibernateDialect()).build();

        properties.forEach(configuration::setProperty);
    }

    protected String getUrl() {
        return String.format("jdbc:%s://%s:%s/%s", this.driverJdbcIdentifier(), this.getAddress(),
                this.getPort(), this.getDatabase());
    }

    protected String getHibernateDialect() {
        switch (this.storageType) {
            case MARIADB:
            case MYSQL:
                return "org.hibernate.dialect.MySQLDialect";
            default:
                throw new IllegalArgumentException();
        }
    }
}
