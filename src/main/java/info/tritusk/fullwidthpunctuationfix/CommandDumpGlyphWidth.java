package info.tritusk.fullwidthpunctuationfix;

import java.io.File;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class CommandDumpGlyphWidth extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        new Thread(() -> {
            try {
                byte[] dataToDump = FullwidthPunctuationFix.INSTANCE.getCharWidthData();
                File dest = new File(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks(), "autogen_glyph_width");
                if (!dest.exists() || !dest.isDirectory()) {
                    dest.mkdir();
                }
                File dumpTargetDir = new File(dest, "assets/minecraft/font");
                if (!dumpTargetDir.exists() || !dumpTargetDir.isDirectory()) {
                    dumpTargetDir.mkdirs();
                }
                File dumpTarget = new File(dumpTargetDir, "glyph_sizes.bin");
                FileUtils.writeByteArrayToFile(dumpTarget, dataToDump, false);
                File packMeta = new File(dest, "pack.mcmeta");
                FileUtils.writeStringToFile(packMeta, "{ \"pack\": { \"pack_format\": 3, \"description\": \"Auto-generated glyph width data\" } }", "UTF-8", false);
                sender.sendMessage(new TextComponentTranslation("command.fwpf.dump.success"));
            } catch (Exception e) {
                sender.sendMessage(new TextComponentTranslation("command.fwpf.dump.fail"));
            }
        }, "GlyphWidthDataExporter").start();
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "dumpglyphwidthdata";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return I18n.format("command.fwpf.dump.usage");
	}

}
