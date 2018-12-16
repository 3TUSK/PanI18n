package info.tritusk.pani18n;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.commands.CommandRegistry;

public final class ZeroDayI18n implements ModInitializer {

    public ZeroDayI18n() {
        /*
         * According to https://stackoverflow.com/questions/361975,
         * the best way to achieve this IS still specifying
         * `-Dfile.encoding=UTF-8' as VM options (i.e. in the
         * Minecraft launcher options, in this specific case).
         *
         * Also, it IS still the best practice to explicitly specify
         * charset whenever it is possible.
         *
         * Mod developer, if you are reading this: please check all your
         * InputStream, Reader, Scanner, ... instances, and make sure
         * they are using StandardCharset.UTF_8, or equivalents.
         *
         * Minecraft Launcher developer, if you are reading this: please
         * consider enforcing `-Dfile.encoding=UTF-8' system property.
         *
         * Thank you for your understanding. Community will appreciate our
         * effort on this.
         */
        //try {
        //    /*
        //     * https://stackoverflow.com/a/14987992
        //     * A somehow dangerous hack.
        //     */
        //    Field f = Charset.class.getDeclaredField("defaultCharset");
        //    f.setAccessible(true);
        //    f.set(null, StandardCharsets.UTF_8);
        //    // Catch all cases
        //    System.setProperty("file.encoding", "UTF-8");
        //} catch (Exception ignored) {}
    }


    @Override
    public void onInitialize() {
        // TODO Can we just swap the FontRenderer used by Minecraft globally?
        // MinecraftClient.getInstance().fontRenderer = ???;
        CommandRegistry.INSTANCE.register(false, FontRendererSwitchCommand::register);
    }
}
