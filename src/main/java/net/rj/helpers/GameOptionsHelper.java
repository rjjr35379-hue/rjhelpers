package net.rj.helpers;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.Arm;

import java.lang.reflect.Field;


public class GameOptionsHelper {

    private static Field mainArmField;

    static {
        try {
            mainArmField = GameOptions.class.getDeclaredField("mainArm");
            mainArmField.setAccessible(true);
        } catch (Exception e) {

            mainArmField = null;
        }
    }


    public static Arm getMainArm(GameOptions options) {
        try {
            if (mainArmField != null) {

                SimpleOption<Arm> mainArmOption = (SimpleOption<Arm>) mainArmField.get(options);
                return mainArmOption.getValue();
            }
        } catch (Exception e) {

        }

        try {

            Object syncedOptions = options.getClass().getMethod("getSyncedOptions").invoke(options);
            return (Arm) syncedOptions.getClass().getMethod("mainArm").invoke(syncedOptions);
        } catch (Exception e) {

        }

        return Arm.RIGHT;
    }
}