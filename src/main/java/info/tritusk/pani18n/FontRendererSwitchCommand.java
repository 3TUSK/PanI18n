package info.tritusk.pani18n;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FontRendererSwitchCommand extends CommandBase {

    private static final String VANILLA_FONT_RENDERER = "vanilla", REVISED_FONT_RENDERER = "i18n";

    @Nonnull
    @Override
    public String getName() {
        return "fontrenderer";
    }

    @Nonnull
    @Override
    public String getUsage(ICommandSender sender) {
        return "/fontrenderer [" + VANILLA_FONT_RENDERER + "|" + REVISED_FONT_RENDERER + "]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, @Nullable String[] args) {
        if (args == null || args.length < 1) {
            return;
        }

        String directive = args[0];

        if (VANILLA_FONT_RENDERER.equals(directive)) {
            Minecraft.getMinecraft().fontRenderer = ZeroDayI18n.originalFontRenderer;
        } else if (REVISED_FONT_RENDERER.equals(directive)) {
            Minecraft.getMinecraft().fontRenderer = ZeroDayI18n.neoFontRenderer;
        }
    }
}
