package info.tritusk.pani18n;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Mod(modid = "0day_i18n", name = "0-Day I18n Bandage", version = "@VERSION@", useMetadata = true, clientSideOnly = true)
public final class ZeroDayI18n {

    static FontRenderer originalFontRenderer;
    static I18nFontRenderer neoFontRenderer;

    public ZeroDayI18n() {
        try {
            Field f = Charset.class.getDeclaredField("defaultCharset");
            f.setAccessible(true);
            f.set(null, StandardCharsets.UTF_8);
            System.setProperty("file.encoding", "UTF-8");
        } catch (Exception ignored) {}
    }

    @Mod.EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        originalFontRenderer = minecraft.fontRenderer;
        ResourceLocation asciiGlyphTexture = new ResourceLocation("textures/font/ascii.png");
        minecraft.fontRenderer = (neoFontRenderer = new I18nFontRenderer(minecraft.gameSettings, asciiGlyphTexture, minecraft.getTextureManager(), minecraft.getLanguageManager().isCurrentLocaleUnicode()));
        ((IReloadableResourceManager) minecraft.getResourceManager()).registerReloadListener(neoFontRenderer);
        ClientCommandHandler.instance.registerCommand(new FontRendererSwitchCommand());
    }

}
