package info.tritusk.fullwidthpunctuationfix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = "fullwidthpunctuationfix", name = "Full-width Punctuation Fix", useMetadata = true, clientSideOnly = true)
public enum FullwidthPunctuationFix {

    INSTANCE;

    private byte[] actualValue = null;

    public static Logger log;

    @Mod.InstanceFactory
    public static FullwidthPunctuationFix getInstance() {
        return INSTANCE;
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPreInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void onPostInit(FMLPostInitializationEvent event) {
        try {
            actualValue = ObfuscationReflectionHelper.getPrivateValue(FontRenderer.class, Minecraft.getMinecraft().fontRendererObj, "glphyWidth", "field_78287_e");
        } catch (Exception e) {
            log.warn("Failed to hack into FontRenderer, FullwidthPunctuationFix will stop working.");
            return;
        }
        ClientCommandHandler.instance.registerCommand(new CommandDumpGlyphWidth());
        ClientCommandHandler.instance.registerCommand(new CommandSetGlyphWidth());
    }

    public void setCharWidth(int index, byte width) {
        if (actualValue != null) {
            this.actualValue[index] = width;
        }
    }

    public byte[] getCharWidthData() {
        byte[] widthData = new byte[65536];
        System.arraycopy(this.actualValue, 0, widthData, 0, 65536);
        return widthData;
    }

}
