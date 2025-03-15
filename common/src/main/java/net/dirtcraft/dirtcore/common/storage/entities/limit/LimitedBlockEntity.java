/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.limit;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.Limitable;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.manager.limit.LimitManager;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "limited_blocks")
public class LimitedBlockEntity implements DirtCoreEntity, Comparable<LimitedBlockEntity> {

    @Transient
    private static final Supplier<Component> LIMIT_REMOVE = () -> Components.REMOVE.build()
            .hoverEvent(HoverEvent.showText(
                    Component.text("Click to remove this limit.", NamedTextColor.RED)));

    @Transient
    private static final Function<String, Component> RULE_ADD = identifier -> Components.ADD.build()
            .hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a rule.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/limit admin edit " + identifier + " rule set "));
    @Transient
    private static final BiFunction<String, LimitManager.Rule, Component> RULE_REMOVE =
            (identifier, rule) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to remove this rule.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.runCommand(
                            "/limit admin edit " + identifier + " rule remove " + rule.name()));
    @Transient
    private static final BiFunction<String, LimitManager.Rule, Component> RULE_CHANGE_VALUE =
            (identifier, rule) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to change the value of this rule.",
                                    NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/limit admin edit " + identifier + " rule set " + rule.name() + ' '));
    @Transient
    private static final Function<String, Component> REASON_ADD =
            identifier -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a reason.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/limit admin edit " + identifier + " reason set "));
    @Transient
    private static final Function<String, Component> REASON_EDIT =
            identifier -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the reason.", NamedTextColor.GOLD))).clickEvent(
                    ClickEvent.suggestCommand("/limit admin edit " + identifier + " reason set "));
    @Transient
    private static final Function<String, Component> REASON_REMOVE =
            identifier -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to remove the reason.", NamedTextColor.RED))).clickEvent(
                    ClickEvent.runCommand("/limit admin edit " + identifier + " reason remove"));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Getter
    @NonNull
    protected String identifier;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String reason;

    @Column(nullable = false)
    @NonNull
    protected String server;

    @NonNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "original", orphanRemoval = true)
    protected Set<LimitedBlockEntryEntity> entries;

    @NonNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "original", orphanRemoval = true)
    protected Set<LimitedBlockRuleEntity> rules;

    protected LimitedBlockEntity() {}

    public LimitedBlockEntity(@NonNull final DirtCorePlugin plugin,
            @NonNull final Limitable limitable) {
        this.identifier = limitable.getIdentifier();
        this.reason = null;
        this.server = plugin.getServerIdentifier();
        this.entries = new HashSet<>();
        this.rules = new HashSet<>();
    }

    public void onRenderDetails(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        builder.add(Component.empty());

        final TextComponent.Builder titleBuilder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(Component.text(" Limited block: ", NamedTextColor.GRAY))
                .append(Component.text(this.identifier, NamedTextColor.GOLD, TextDecoration.BOLD));

        // LimitCommand.COMMAND_LIMIT_ADMIN_REMOVE.build(sender, this.identifier).ifPresent(
        //         s -> titleBuilder.appendSpace()
        //                 .append(LIMIT_REMOVE.get().clickEvent(ClickEvent.runCommand(s))));

        builder.add(titleBuilder.build())
                .add(Component.empty());

        final TextComponent.Builder reasonBuilder = Component.text()
                .append(Component.text("Reason: ", NamedTextColor.GOLD));

        if (this.reason == null) {
            reasonBuilder.append(Component.text("No reason specified.", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.LIMIT_ADMIN_EDIT_REASON)) {
                reasonBuilder.appendSpace()
                        .append(REASON_ADD.apply(this.identifier));
            }
        } else {
            reasonBuilder.append(Component.text(this.reason));

            if (sender.hasPermission(Permission.LIMIT_ADMIN_EDIT_REASON)) {
                reasonBuilder.appendSpace()
                        .append(REASON_EDIT.apply(this.identifier))
                        .append(REASON_REMOVE.apply(this.identifier));
            }
        }

        builder.add(reasonBuilder.build());

        final TextComponent.Builder rulesBuilder = Component.text()
                .append(Component.text("Rules: ", NamedTextColor.GOLD));

        if (sender.hasPermission(Permission.LIMIT_ADMIN_EDIT_RULE)) {
            rulesBuilder.append(RULE_ADD.apply(this.identifier));
        }

        builder.add(rulesBuilder.build());

        final Map<LimitManager.Rule, Long> ruleMap = this.getRuleMap();

        for (final Map.Entry<LimitManager.Rule, Long> entry : ruleMap.entrySet()) {
            final LimitManager.Rule key = entry.getKey();
            final TextComponent.Builder ruleBuilder = Component.text()
                    .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                    .append(Component.text()
                            .append(Component.text(key.name(), key.getColor()))
                            .append(Component.text(" | ", NamedTextColor.GRAY))
                            .append(Component.text(entry.getValue(), NamedTextColor.DARK_AQUA))
                            .hoverEvent(HoverEvent.showText(
                                    Component.text(key.getDescription(), key.getColor()))))
                    .appendSpace()
                        .append(RULE_CHANGE_VALUE.apply(this.identifier, key));

            // only give option to remove when there is more than one rule
            if (ruleMap.size() > 1) {
                ruleBuilder.append(RULE_REMOVE.apply(this.identifier, key));
            }

            builder.add(ruleBuilder.build());
        }

        builder.add(Component.empty());
    }

    @NonNull
    public Map<LimitManager.Rule, Long> getRuleMap() {
        final EnumMap<LimitManager.Rule, Long> map = new EnumMap<>(LimitManager.Rule.class);

        for (final LimitedBlockRuleEntity limitedBlockRule : this.rules) {
            try {
                final LimitManager.Rule rule =
                        LimitManager.Rule.valueOf(limitedBlockRule.getRule());
                map.put(rule, limitedBlockRule.getAmount());
            } catch (final IllegalArgumentException ignored) {}
        }

        return map;
    }

    public void addRule(final @NonNull LimitedBlockRuleEntity rule) {
        this.rules.add(rule);
    }

    public boolean removeRule(@NonNull final TaskContext context,
            final LimitManager.@NonNull Rule rule) {
        final String ruleName = rule.name();
        LimitedBlockRuleEntity limitedBlockRule = null;

        for (final LimitedBlockRuleEntity lbr : this.rules) {
            if (lbr.getRule().equals(ruleName)) {
                limitedBlockRule = lbr;
                break;
            }
        }

        if (limitedBlockRule == null) {
            return false;
        }

        this.rules.remove(limitedBlockRule);
        context.session().remove(limitedBlockRule);
        return true;
    }

    public void setValue(@NonNull final TaskContext context, final LimitManager.@NonNull Rule rule,
            final long amount) {
        final String ruleName = rule.name();
        LimitedBlockRuleEntity limitedBlockRule = null;

        for (final LimitedBlockRuleEntity lbr : this.rules) {
            if (lbr.getRule().equals(ruleName)) {
                limitedBlockRule = lbr;
                break;
            }
        }

        if (limitedBlockRule == null) {
            // not found, create
            limitedBlockRule = new LimitedBlockRuleEntity(this, rule, amount);
            context.session().persist(limitedBlockRule);
            this.addRule(limitedBlockRule);
        } else {
            limitedBlockRule.setAmount(amount);
            context.session().merge(limitedBlockRule);
        }
    }

    @NonNull
    public Optional<LimitManager.Result> checkRuleViolation(@NonNull final UUID uniqueId,
            @NonNull final Collection<LimitedBlockEntryEntity> limitedBlockEntries,
            @NonNull final World world, final int x, final int z) {
        for (final Map.Entry<LimitManager.Rule, Long> entry : this.getRuleMap().entrySet()) {
            final LimitManager.Result result = entry.getKey()
                    .getResult(uniqueId, limitedBlockEntries, entry.getValue(), world, x, z);

            if (result.violates()) {
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    @Override
    public int compareTo(@NotNull final LimitedBlockEntity other) {
        return this.identifier.compareTo(other.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        return this.compareTo((LimitedBlockEntity) o) == 0;
    }
}
