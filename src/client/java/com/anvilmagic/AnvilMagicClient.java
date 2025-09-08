package com.anvilmagic;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AnvilMagicClient implements ClientModInitializer {
    public static final String MOD_ID = "anvilmagic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("AnvilMagic 客户端模组已加载 - 拆分附魔书功能已启用");
    }
}