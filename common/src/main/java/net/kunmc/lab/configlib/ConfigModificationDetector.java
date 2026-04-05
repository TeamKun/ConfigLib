package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.util.ConfigUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.*;

class ConfigModificationDetector {
    private final CommonBaseConfig config;
    private final Map<Field, Pair<Object, Integer>> fieldToObjectAndHashMap = new HashMap<>();

    ConfigModificationDetector(CommonBaseConfig config) {
        this.config = config;
    }

    void initializeHash() {
        ConfigUtil.getObservedFields(config, Object.class)
                  .forEach(x -> {
                      try {
                          Object o = x.get(config);
                          if (o instanceof Value<?, ?>) {
                              Value<?, ?> value = (Value<?, ?>) o;
                              fieldToObjectAndHashMap.put(x, Pair.of(value, value.valueHashCode()));
                          } else {
                              fieldToObjectAndHashMap.put(x, Pair.of(o, Objects.hashCode(o)));
                          }
                      } catch (IllegalAccessException e) {
                          throw new RuntimeException(e);
                      }
                  });
    }

    void start(Timer timer, int period) {
        timer.scheduleAtFixedRate(new DetectionTask(), 0, period);
    }

    private class DetectionTask extends TimerTask {
        @Override
        public void run() {
            if (!config.initialized) {
                return;
            }

            synchronized (config.ioLock) {
                boolean modified = false;
                for (Map.Entry<Field, Pair<Object, Integer>> entry : fieldToObjectAndHashMap.entrySet()) {
                    Field field = entry.getKey();
                    Object o = entry.getValue()
                                    .getKey();
                    int oldHash = entry.getValue()
                                       .getValue();
                    if (o instanceof Value) {
                        Value value = (Value) o;
                        int newHash = value.valueHashCode();
                        if (newHash != oldHash) {
                            config.pushHistory();
                            value.dispatchModify(value.value());
                            fieldToObjectAndHashMap.put(field, Pair.of(value, newHash));
                            modified = true;
                        }
                    } else {
                        try {
                            // 通常のクラスだとインスタンスが変わっている可能性があるため再取得
                            Object newObj = field.get(config);
                            int newHash = Objects.hashCode(newObj);
                            if (newHash != oldHash) {
                                fieldToObjectAndHashMap.put(field, Pair.of(newObj, newHash));
                                modified = true;
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (modified) {
                    config.saveConfigIfPresent();
                }
            }
        }
    }
}
