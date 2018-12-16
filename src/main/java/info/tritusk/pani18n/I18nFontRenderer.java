package info.tritusk.pani18n;

import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.texture.TextureManager;

import java.util.List;
import java.util.Locale;

public final class I18nFontRenderer extends FontRenderer {

    public I18nFontRenderer(TextureManager textureManager, FontStorage fontStorage) {
        super(textureManager, fontStorage);
    }

    @Override
    public final List<String> wrapStringToWidthAsList(final String str, final int wrapWidth) {
        // FIXME getCharWith now returns float, not int
        // FIXME We need to parse language code in order to obtain the correct java.util.Locale object
        return FormattingEngine.wrapStringToWidth(str, wrapWidth, c -> (int)this.getCharWidth((char)c), Locale.getDefault());
    }
}
