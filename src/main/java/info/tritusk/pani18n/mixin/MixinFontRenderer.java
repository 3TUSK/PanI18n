package info.tritusk.pani18n.mixin;

import info.tritusk.pani18n.FormattingEngine;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import net.minecraft.client.font.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Locale;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {


    /**
     * @param str source string
     * @param wrapWidth max. length before the inserted new line
     * @return list of wrapped strings
     *
     * @see FormattingEngine#wrapStringToWidth(String, int, Char2FloatFunction, Locale)
     *
     * @author 3TUSK
     */
    @Overwrite
    public List<String> wrapStringToWidthAsList(final String str, final int wrapWidth) {
        return FormattingEngine.wrapStringToWidth(str, wrapWidth, this::getCharWidth, Locale.getDefault());
    }

    // TODO The cursor becomes wonky now. Needs investigation
    @Shadow
    public abstract float getCharWidth(char c);
}
