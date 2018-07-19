package info.tritusk.pani18n;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.annotation.Nonnull;
import java.util.List;

public final class I18nFontRenderer extends FontRenderer {

    I18nFontRenderer(GameSettings settings, ResourceLocation asciiGlyph, TextureManager textureManager, boolean unicode) {
        super(settings, asciiGlyph, textureManager, unicode);
    }

    @Nonnull
    @Override
    public final List<String> listFormattedStringToWidth(final String str, final int wrapWidth) {
        return FormattingEngine.wrapStringToWidth(str, wrapWidth, c -> this.getCharWidth((char)c), MinecraftForgeClient.getLocale());
    }
}
