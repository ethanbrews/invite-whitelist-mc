package me.ethanbrews.whitelistinvite;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collection;
import java.util.Date;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WhitelistInvite implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
    private static final Identifier INVITE_DATA = new Identifier("whitelistinvite","data");

    @Override
    public void onInitialize() {

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("invite")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            if(context.getSource().isExecutedByPlayer())
                            {
                                var it = GameProfileArgumentType.getProfileArgument(context,"player");
                                NbtCompound nbt = context.getSource().getServer().getDataCommandStorage().get(INVITE_DATA);
                                if(nbt.isEmpty())
                                {
                                    nbt = new NbtCompound();
                                }
                                for(GameProfile gp: it)
                                {


                                        context.getSource().getServer().getPlayerManager().getWhitelist().add(new WhitelistEntry(gp));
                                        NbtCompound gpdata = new NbtCompound();
                                        gpdata.putString("inviter",context.getSource().getPlayer().getUuidAsString());
                                        gpdata.putString("time", Instant.now().toString());
                                        nbt.put(gp.getId().toString(),gpdata);
                                        context.getSource().sendMessage(Text.of("Added "+gp.getName()+" to whitelist, you can /revoke them for 10 days!"));


                                }
                                context.getSource().getServer().getDataCommandStorage().set(INVITE_DATA,nbt);
                                return 1;
                            }
                            context.getSource().sendMessage(Text.of("This command must be executed by a player, otherwise use normal /whitelist commands"));
                            return 0;
                        }))

                ));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("revoke")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            if(context.getSource().isExecutedByPlayer())
                            {
                                var it = GameProfileArgumentType.getProfileArgument(context,"player");
                                NbtCompound nbt = context.getSource().getServer().getDataCommandStorage().get(INVITE_DATA);

                                for(GameProfile gp: it)
                                {
                                    if(nbt.contains(gp.getId().toString()))
                                    {
                                        NbtCompound gpdata = nbt.getCompound(gp.getId().toString());
                                        if(gpdata.getString("inviter").equals(context.getSource().getPlayer().getUuidAsString()))
                                        {
                                            if(Time.from(Instant.parse(gpdata.getString("time"))).before(Date.from(Instant.now().plus(10, ChronoUnit.DAYS))))
                                            {
                                                context.getSource().sendMessage(Text.of(gp.getName()+" has been revoked from the whitelist!"));
                                                context.getSource().getServer().getPlayerManager().getWhitelist().remove(gp);
                                            }
                                            else
                                            {
                                                context.getSource().sendMessage(Text.of("You cannot revoke a player you invited after 10 days!"));
                                            }

                                        }
                                    }
                                    else
                                    {
                                        context.getSource().sendMessage(Text.of("You didn't invite that player!"));
                                    }
                                }
                                return 1;
                            }
                            context.getSource().sendMessage(Text.of("This command must be executed by a player, otherwise use normal /whitelist commands"));
                            return 0;
                        }))

        ));
        LOGGER.info("Hello Fabric world!");
    }
}