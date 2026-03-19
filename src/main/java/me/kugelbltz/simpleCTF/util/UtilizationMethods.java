package me.kugelbltz.simpleCTF.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class UtilizationMethods {

    public static Component serializeMessage(String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

}
