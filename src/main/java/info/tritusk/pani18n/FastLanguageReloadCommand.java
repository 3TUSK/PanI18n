package info.tritusk.pani18n;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;

public class FastLanguageReloadCommand extends CommandBase {
    @Override
    public String getName() {
        return "reload_lang";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reload_lang";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (ForgeModContainer.selectiveResourceReloadEnabled) {
            FMLClientHandler.instance().refreshResources(VanillaResourceType.LANGUAGES);
            sender.sendMessage(new TextComponentTranslation("command.0day_i18n.fast_lang_reload.success"));
        } else {
            throw new CommandException("command.0day_i18n.fast_lang_reload.error");
        }
    }
}
