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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

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
                try {
                    Files.createDirectory(dest.toPath());
                } catch (FileAlreadyExistsException e) {}
                File dumpTargetDir = new File(dest, "assets/minecraft/font");
                try {
                    Files.createDirectories(dumpTargetDir.toPath());
                } catch (FileAlreadyExistsException e) {}
                File dumpTarget = new File(dumpTargetDir, "glyph_sizes.bin");
                try {
                    Files.createFile(dumpTarget.toPath());
                } catch (FileAlreadyExistsException e) {
                    dumpTarget.delete();
                    dumpTarget.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(dumpTarget);
                outputStream.write(dataToDump);
                outputStream.close();
                File packMeta = new File(dest, "pack.mcmeta");
                if (!packMeta.exists()) {
                    PrintWriter writer = new PrintWriter(packMeta);
                    writer.println("{ \"pack\": { \"pack_format\": 2, \"description\": \"Auto-generated glyph width data\" } }");
                    writer.flush();
                    writer.close();
                }
                sender.addChatMessage(new TextComponentTranslation("command.fwpf.dump.success"));
            } catch (Exception e) {
                sender.addChatMessage(new TextComponentTranslation("command.fwpf.dump.fail"));
            }
        }, "GlyphWidthDataExporter").start();
    }

}
