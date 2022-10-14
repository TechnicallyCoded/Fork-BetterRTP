package me.SuperRonanCraft.BetterRTP.references.depends;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.player.rtp.RTPSetupInformation;
import me.SuperRonanCraft.BetterRTP.references.PermissionNode;
import me.SuperRonanCraft.BetterRTP.references.helpers.*;
import me.SuperRonanCraft.BetterRTP.references.player.HelperPlayer;
import me.SuperRonanCraft.BetterRTP.references.player.playerdata.PlayerData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.CooldownData;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WorldPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DepPlaceholderAPI extends PlaceholderExpansion {

    @NotNull
    @Override
    public String getIdentifier() {
        return BetterRTP.getInstance().getDescription().getName().toLowerCase();
    }

    @NotNull
    @Override
    public String getAuthor() {
        return BetterRTP.getInstance().getDescription().getAuthors().get(0);
    }

    @NotNull
    @Override
    public String getVersion() {
        return BetterRTP.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String request) {
        PlayerData data = HelperPlayer.getData(player);
        if (request.equalsIgnoreCase("count")) {
            return String.valueOf(data.getRtpCount());
        } else if (request.startsWith("cooldown")) {
            if (request.equalsIgnoreCase("cooldown")) {
                return cooldown(data, player.getWorld());
            } else if (request.startsWith("cooldown_")) {
                World world = getWorld(request.replace("cooldown_", ""));
                return cooldown(data, world);
            } else if (request.equalsIgnoreCase("cooldowntime")) {
                return cooldownTime(data, player.getWorld());
            } else if (request.startsWith("cooldowntime_")) {
                World world = getWorld(request.replace("cooldowntime_", ""));
                return cooldownTime(data, world);
            }
        } else if (request.startsWith("canrtp_")) {
            World world = getWorld(request.replace("canrtp_", ""));
            return canRTP(player, world);
        } else if (request.equalsIgnoreCase("canrtp")) {
            World world = player.getWorld();
            return canRTP(player, world);
        }
        return null;
    }

    private String cooldown(PlayerData data, World world) {
        if (world == null) return "Invalid World";
        long lng = BetterRTP.getInstance().getCooldowns().locked(data.player) ? -1L :
                HelperRTP_Check.getCooldown(data.player, HelperRTP.getPlayerWorld(new RTPSetupInformation(world, data.player, data.player, true)));
        return HelperDate.total(lng);
    }

    private String cooldownTime(PlayerData data, World world) {
        if (world == null) return "Invalid World";
        RTPSetupInformation setup_info = new RTPSetupInformation(HelperRTP.getActualWorld(data.player, world), data.player, data.player, true);
        WorldPlayer pWorld = HelperRTP.getPlayerWorld(setup_info);
        Long cooldownTime = BetterRTP.getInstance().getCooldowns().locked(data.player) ? -1L :
                (HelperRTP_Check.applyCooldown(data.player, data.player) ? pWorld.getCooldown() * 1000L : 0L);
        return HelperDate.total(cooldownTime);
    }

    private String canRTP(Player player, World world) {
        if (world == null) return "Invalid World";
        world = HelperRTP.getActualWorld(player, world);
        //Permission
        if (!PermissionNode.getAWorld(player, world.getName()))
            return BetterRTP.getInstance().getSettings().getPlaceholder_nopermission();
        RTPSetupInformation setupInformation = new RTPSetupInformation(world, player, player, true);
        WorldPlayer pWorld = HelperRTP.getPlayerWorld(setupInformation);
        //Cooldown
        if (HelperRTP_Check.isCoolingDown(player, player, pWorld))
            return BetterRTP.getInstance().getSettings().getPlaceholder_cooldown();
        //Price
        if (!BetterRTP.getInstance().getEco().hasBalance(player, pWorld))
            return BetterRTP.getInstance().getSettings().getPlaceholder_balance();
        //Hunger
        if (!BetterRTP.getInstance().getEco().hasHunger(player, pWorld))
            return BetterRTP.getInstance().getSettings().getPlaceholder_hunger();
        //True
        return BetterRTP.getInstance().getSettings().getPlaceholder_true();
    }

    private World getWorld(String world_name) {
        World world = null;
        if (world_name.length() > 0)
            for (World _world : Bukkit.getWorlds())
                if (world_name.equalsIgnoreCase(_world.getName())) {
                    world = _world;
                    break;
                }
        return world;
    }
}