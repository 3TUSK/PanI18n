package info.tritusk.pani18n;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

final class FontRendererSwitchCommand {

    private static final String VANILLA_FONT_RENDERER = "vanilla", REVISED_FONT_RENDERER = "i18n";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder
                .<ServerCommandSource>literal("fontrenderer")
                .then(LiteralArgumentBuilder
                        .<ServerCommandSource>literal(VANILLA_FONT_RENDERER)
                        .executes(FontRendererSwitchCommand::setVanillaFontRenderer))
                .then(LiteralArgumentBuilder
                        .<ServerCommandSource>literal(REVISED_FONT_RENDERER)
                        .executes(FontRendererSwitchCommand::setRevisedFontRenderer))
        );
    }

    private static int setVanillaFontRenderer(CommandContext<ServerCommandSource> context) {
        /*
         * FIXME
         * MinecraftClient.getInstance().fontRenderer = ZeroDayI18n.neoFontRenderer;
         */
        return Command.SINGLE_SUCCESS;
    }

    private static int setRevisedFontRenderer(CommandContext<ServerCommandSource> context) {
        /*
         * FIXME
         * MinecraftClient.getInstance().fontRenderer = ZeroDayI18n.neoFontRenderer;
         */
        return Command.SINGLE_SUCCESS;
    }

}
