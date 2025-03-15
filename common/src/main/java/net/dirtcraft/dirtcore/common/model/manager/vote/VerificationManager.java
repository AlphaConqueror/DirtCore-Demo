/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.model.manager.vote;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.storage.entities.VerificationEntity;
import net.dirtcraft.storageutils.taskcontext.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface VerificationManager {

    String ALPHABET = "0123456789ABCDEF";
    short CODE_LENGTH = 6;
    SecureRandom RANDOM = new SecureRandom();
    long ABOUT_TO_EXPIRE_MINUTES = 1;
    long EXPIRY_MINUTES = 10;

    @NonNull Optional<VerificationEntity> getVerification(@NonNull TaskContext context,
            long discordUserId);

    @NonNull Optional<VerificationEntity> getVerification(@NonNull TaskContext context,
            @NonNull UUID minecraftUniqueId);

    @NonNull VerificationEntity generateCode(@NonNull TaskContext context, long discordUserId);

    /**
     * Link a Discord user to a Minecraft account.
     *
     * @param context           the task context
     * @param discordUserId     the discord user id
     * @param minecraftUniqueId the Minecraft unique id
     * @return True, if successful. False, if the user is already linked.
     */
    boolean link(@NonNull TaskContext context, long discordUserId, @NonNull UUID minecraftUniqueId);

    /**
     * Unlink a Discord user from a Minecraft account.
     *
     * @param context       the task context
     * @param discordUserId the Discord user id
     * @return True, if successful. False, if the user is not linked.
     */
    boolean unlink(@NonNull TaskContext context, long discordUserId);

    /**
     * Unlink a Minecraft account from a Discord user.
     *
     * @param context           the task context
     * @param minecraftUniqueId the Minecraft account unique id
     * @return True, if successful. False, if the user is not linked.
     */
    boolean unlink(@NonNull TaskContext context, @NonNull UUID minecraftUniqueId);

    /**
     * Verify a Minecraft account using a code.
     *
     * @param context           the task context
     * @param minecraftUniqueId the unique id of the Minecraft account
     * @param code              the code
     * @return the {@link VerificationEntity} object if successful, null if not
     */
    @Nullable VerificationEntity verify(@NonNull TaskContext context,
            @NonNull UUID minecraftUniqueId, @NonNull String code);

    /**
     * Unverify a Minecraft account.
     *
     * @param context           the task context
     * @param minecraftUniqueId the unique id of the Minecraft account
     * @return the removed {@link VerificationEntity} object if successful, null if not
     */
    @Nullable VerificationEntity unverify(@NonNull TaskContext context,
            @NonNull UUID minecraftUniqueId);
}
