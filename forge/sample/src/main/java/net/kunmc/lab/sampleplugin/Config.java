package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.IntegerValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public IntegerValue normalIntVal = new IntegerValue(128);
    //setコマンドから変更不可のインスタンス
    public IntegerValue readOnlyIntVal = new IntegerValue(256).writableByCommand(false);
    //listコマンドに表示されないインスタンス
    public IntegerValue writeOnlyIntVal = new IntegerValue(512).listable(false);
    //jsonに保存されないフィールド
    public transient IntegerValue transientIntVal = new IntegerValue(1024);

    public Config(@NotNull Plugin plugin, @NotNull String entryName) {
        super(plugin, entryName);
    }

    // Outputs

    /* /sample config list
        normalIntVal: 128
        readOnlyIntVal: 256
        transientIntVal: 1024
     */

    /* /sample config set
        normalIntVal
        transientIntVal
        writeOnlyIntVal
     */

    /* JSON
        {
          "normalIntVal": {
            "value": 128,
            "min": -2147483648,
            "max": 2147483647
          },
          "readOnlyIntVal": {
            "value": 256,
            "min": -2147483648,
            "max": 2147483647
          },
          "writeOnlyIntVal": {
            "value": 512,
            "min": -2147483648,
            "max": 2147483647
          }
        }
     */
}
