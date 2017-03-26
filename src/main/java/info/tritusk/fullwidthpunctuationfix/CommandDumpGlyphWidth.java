package info.tritusk.fullwidthpunctuationfix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class CommandDumpGlyphWidth extends CommandBase {

    @Override
    @Nonnull
    public String getCommandName() {
        return "dumpglyphwidthdata";
    }

    @Override
    @Nonnull
    public String getCommandUsage(ICommandSender sender) {
        return I18n.format("command.fwpf.dump.usage");
    }

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
                FileUtils.writeStringToFile(packMeta, "{ \"pack\": { \"pack_format\": 2, \"description\": \"Auto-generated glyph width data\" } }", "UTF-8", false);
                sender.addChatMessage(new TextComponentTranslation("command.fwpf.dump.success"));
            } catch (Exception e) {
                sender.addChatMessage(new TextComponentTranslation("command.fwpf.dump.fail"));
            }
        }, "GlyphWidthDataExporter").start();
    }

}
