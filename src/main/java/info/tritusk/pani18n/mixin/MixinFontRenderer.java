package info.tritusk.pani18n.mixin;

import info.tritusk.pani18n.FormattingEngine;
import net.minecraft.client.font.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Locale;
import java.util.function.IntUnaryOperator;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {


    /**
     * @param str source string
     * @param wrapWidth max. length before the inserted new line
     * @return list of wrapped strings
     *
     * @see FormattingEngine#wrapStringToWidth(String, int, IntUnaryOperator, Locale)
     *
     * @author 3TUSK
     */
    @Overwrite
    public List<String> wrapStringToWidthAsList(final String str, final int wrapWidth) {
        return FormattingEngine.wrapStringToWidth(str, wrapWidth, c -> (int)this.getCharWidth((char)c), Locale.getDefault());
    }

    @Shadow
    public abstract float getCharWidth(char c);
}
