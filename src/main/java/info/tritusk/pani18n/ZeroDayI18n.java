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
        /*
         * According to https://stackoverflow.com/questions/361975,
         * the best way to achieve this IS still specifying
         * `-Dfile.encoding=UTF-8' as VM options (i.e. in the
         * Minecraft launcher options, in this specific case).
         *
         * Also, it IS still the best practice to explicitly specify
         * charset whenever it is possible.
         *
         * Mod developer, if you are reading this: please check all your
         * InputStream, Reader, Scanner, ... instances, and make sure
         * they are using StandardCharset.UTF_8, or equivalents.
         *
         * Minecraft Launcher developer, if you are reading this: please
         * consider enforcing `-Dfile.encoding=UTF-8' system property.
         *
         * Thank you for your understanding. Community will appreciate our
         * effort on this.
         */
        try {
            /*
             * https://stackoverflow.com/a/14987992
             * A somehow dangerous hack.
             */
            Field f = Charset.class.getDeclaredField("defaultCharset");
            f.setAccessible(true);
            f.set(null, StandardCharsets.UTF_8);
            // Catch all cases
            System.setProperty("file.encoding", "UTF-8");
        } catch (Exception ignored) {}
    }

    @Mod.EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        // Keep a reference to the original one for debug purpose
        originalFontRenderer = minecraft.fontRenderer;
        ResourceLocation asciiGlyphTexture = new ResourceLocation("textures/font/ascii.png");
        // This is the FontRenderer that almost the whole Minecraft relies on. Swap it out.
        minecraft.fontRenderer = (neoFontRenderer = new I18nFontRenderer(minecraft.gameSettings, asciiGlyphTexture, minecraft.getTextureManager(), minecraft.getLanguageManager().isCurrentLocaleUnicode()));
        // Register this to the resource reloading listener to make sure everything is ok
        ((IReloadableResourceManager) minecraft.getResourceManager()).registerReloadListener(neoFontRenderer);
        ClientCommandHandler.instance.registerCommand(new FontRendererSwitchCommand());
    }

}
