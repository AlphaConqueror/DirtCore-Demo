/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.user;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "user_settings")
public class UserSettingsEntity implements DirtCoreEntity {

    @Id
    @Column(length = 36, nullable = false, unique = true)
    @NonNull
    protected String unique_id;

    @Column(name = "read_local", nullable = false)
    @Getter
    @Setter
    protected boolean readLocal;

    @Column(name = "read_global", nullable = false)
    @Getter
    @Setter
    protected boolean readGlobal;

    @Column(name = "read_staff_local", nullable = false)
    @Getter
    @Setter
    protected boolean readStaffLocal;

    @Column(name = "read_staff_global", nullable = false)
    @Getter
    @Setter
    protected boolean readStaffGlobal;

    @Column(name = "write_channel", nullable = false)
    @NonNull
    protected String writeChannel;

    @Column(name = "social_spy", nullable = false)
    @Getter
    @Setter
    protected boolean socialSpy;

    protected UserSettingsEntity() {}

    public UserSettingsEntity(@NonNull final UUID uniqueId) {
        this.unique_id = uniqueId.toString();
        this.readLocal = true;
        this.readGlobal = true;
        this.readStaffLocal = true;
        this.readStaffGlobal = true;
        this.writeChannel = MessagingManager.ChannelType.LOCAL.getIdentifier();
        this.socialSpy = false;
    }

    @NonNull
    public UUID getUniqueId() {
        return UUID.fromString(this.unique_id);
    }

    public MessagingManager.@NonNull ChannelType getWriteChannel() {
        return MessagingManager.ChannelType.fromIdentifier(this.writeChannel);
    }

    public void setWriteChannel(final MessagingManager.@NonNull ChannelType channel) {
        this.writeChannel = channel.getIdentifier();
    }
}
