package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.command.AccessibleCommand;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;

class ConfigGetCommand extends AccessibleCommand {
    public ConfigGetCommand(BaseConfig config) {
        super(SubCommandType.Get.name);

        init(config, this);
    }

    public ConfigGetCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Get.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (BaseConfig config : configSet) {
            children(new AccessibleCommand(config.entryName()) {
                {
                    init(config, this);
                }
            });
        }
    }

    private void init(BaseConfig config, AccessibleCommand command) {
        for (Field field : config.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            Object o;
            try {
                o = field.get(config);
            } catch (Exception e) {
                continue;
            }

            if (o instanceof Value) {
                Value<?> v = ((Value<?>) o);
                if (v.listable()) {
                    command.appendChild(new Command(field.getName()) {
                        @Override
                        public void execute(@NotNull CommandContext ctx) {
                            v.sendListMessage(ctx, field.getName());
                        }
                    });
                }
            }
        }
    }
}
