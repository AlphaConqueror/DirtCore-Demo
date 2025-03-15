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

package net.dirtcraft.dirtcore.common.storage.entities.crate.content;

import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crate_content_items")
public class CrateContentItemEntity implements Comparable<CrateContentItemEntity>, DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected CrateContentEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    @Setter
    protected String identifier;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String persistentData;

    protected CrateContentItemEntity() {}

    public CrateContentItemEntity(@NonNull final CrateContentEntity original,
            @NonNull final ItemStack itemStack) {
        this.original = original;
        this.update(itemStack);
    }

    public void update(@NonNull final ItemStack itemStack) {
        this.identifier = itemStack.getIdentifier();
        this.persistentData = itemStack.getPersistentDataAsString();
    }

    @NonNull
    public Optional<ItemStack> asItemStack(@NonNull final DirtCorePlugin plugin) {
        return plugin.getPlatformFactory().createItemStack(this.identifier, 1, this.persistentData);
    }

    @Override
    public int compareTo(@NotNull final CrateContentItemEntity other) {
        return Long.compare(this.id, other.id);
    }
}
