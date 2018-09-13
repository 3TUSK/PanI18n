package info.tritusk.fullwidthpunctuationfix;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class CommandSetGlyphWidth extends CommandBase {

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

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "setglyphwidth";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return I18n.format("command.fwpf.set.usage");
	}

}
