package info.tritusk.fullwidthpunctuationfix;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class CommandSetGlyphWidth extends CommandBase {

    @Override
    @Nonnull
    public String getCommandName() {
        return "setglyphwidth";
    }

    @Override
    @Nonnull
    public String getCommandUsage(ICommandSender sender) {
        return I18n.format("command.fwpf.set.usage");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            char c = args[0].charAt(0);
            byte width = Byte.parseByte(args[1]);
            FullwidthPunctuationFix.INSTANCE.setCharWidth((int)c, width);
        } catch (Exception e) {
            throw new CommandException(I18n.format("command.fwpf.set.fail"));
        }
    }

}
