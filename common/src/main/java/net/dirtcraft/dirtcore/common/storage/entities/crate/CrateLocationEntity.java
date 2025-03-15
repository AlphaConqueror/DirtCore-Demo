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

package net.dirtcraft.dirtcore.common.storage.entities.crate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3i;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crate_locations")
public class CrateLocationEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    protected long id;

    @Getter
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected CrateEntity original;

    @Column(nullable = false)
    @Getter
    protected String world;

    @Column(nullable = false)
    @Getter
    protected int x;

    @Column(nullable = false)
    @Getter
    protected int y;

    @Column(nullable = false)
    @Getter
    protected int z;

    protected CrateLocationEntity() {}

    public CrateLocationEntity(@NonNull final CrateEntity original, @NonNull final World world,
            @NonNull final Vec3i vec3i) {
        this(original, world, vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public CrateLocationEntity(@NonNull final CrateEntity original, @NonNull final World world,
            final int x, final int y, final int z) {
        this.original = original;
        this.world = world.getIdentifier();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NonNull
    public BlockPos asBlockPos() {
        return BlockPos.of(this.x, this.y, this.z);
    }
}
