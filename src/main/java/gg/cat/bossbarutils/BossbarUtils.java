package gg.cat.bossbarutils;

import com.google.common.eventbus.EventBus;
import gg.cat.bossbarutils.mixin.BossBarsAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;

import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class BossbarUtils implements ModInitializer {
    public static final ModMetadata MOD_META;

    public static final String MOD_ID = "bossbarutils";
    public static final String VERSION = "1.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EventBus EVENT_BUS = new EventBus();

    public static MinecraftClient mc;
    public static long initTime;

    static {
        MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
    }

    @Override
    public void onInitialize() {
        mc = MinecraftClient.getInstance();
        initTime = System.currentTimeMillis();

        EVENT_BUS.register(this);
        LOGGER.info("[bossbarutils] init time: {} ms.", System.currentTimeMillis() - initTime);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("printa")
                        .executes(context -> {
                            grabBossbars();
                            return 1;
                        })
            );
        });
    }

    public void grabBossbars() {
        if (mc.player != null && mc.world != null) {
            BossBarsAccessor accessor = (BossBarsAccessor) mc.inGameHud.getBossBarHud();
            Map<UUID, ClientBossBar> bossBars = accessor.getBossBars();

            if (!bossBars.isEmpty()) {
                for (Map.Entry<UUID, ClientBossBar> entry : bossBars.entrySet()) {
                    UUID id = entry.getKey();
                    ClientBossBar bossBar = entry.getValue();
                    String bossBarId = id.toString();
                    Text bossBarName = bossBar.getName();
                    String bossBarColor = bossBar.getColor().getName();
                    String bossBarStyle = bossBar.getStyle().getName();
                    float bossBarValue = bossBar.getPercent();

                    String addCommand = "/bossbar add " + bossBarId + " \"" + bossBarName.getString() + "\"";
                    String colorCommand = "/bossbar set " + bossBarId + " color " + bossBarColor;
                    String styleCommand = "/bossbar set " + bossBarId + " style " + bossBarStyle;
                    String valueCommand = "/bossbar set " + bossBarId + " value " + bossBarValue;

                    mc.player.sendMessage(Text.of("Entry -------- Start"), false);
                    mc.player.sendMessage(Text.of(addCommand), false);
                    mc.player.sendMessage(Text.of(colorCommand), false);
                    mc.player.sendMessage(Text.of(styleCommand), false);
                    mc.player.sendMessage(Text.of(valueCommand), false);
                    LOGGER.info(String.valueOf(bossBarName));
                    LOGGER.info(String.valueOf(bossBar.getStyle()));
                    LOGGER.info(String.valueOf(bossBar.getColor()));
                    LOGGER.info(String.valueOf(bossBar.getColor().getTextFormat()));
                    mc.player.sendMessage(Text.of("Entry -------- End"), false);
                }
            } else {
                mc.player.sendMessage(Text.of("No boss bars found!"), false);
            }
        }
    }
}
