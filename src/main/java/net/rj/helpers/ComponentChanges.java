package net.rj.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class ComponentChanges {

    private final Map<String, NbtElement> additions; //things to add or rmeove
    private final Set<String> removals; //remove

    private ComponentChanges(Map<String, NbtElement> additions, Set<String> removals) {
        this.additions = additions;
        this.removals = removals;
    }
    public Set<String> getAdditions() {
        return additions.keySet();
    }
    public Set<String> getRemovals() {
        return removals;
    }
    @Nullable
    public NbtElement get(String key) {
        return additions.get(key);
    }
    public boolean hasAddition(String key) {
        return additions.containsKey(key);
    }
    public boolean hasRemoval(String key) {
        return removals.contains(key);
    }
    public boolean isEmpty() {
        return additions.isEmpty() && removals.isEmpty();
    }
    public Set<Map.Entry<String, NbtElement>> entrySet() {
        return additions.entrySet();
    }
    public void applyTo(ItemStack stack) { // apply addition and rmvoals
        NbtCompound nbt = stack.getOrCreateNbt();
        for (Map.Entry<String, NbtElement> entry : additions.entrySet()) {
            String key = entry.getKey();
            NbtElement value = entry.getValue();
            if (key.contains(".")) {
                String[] parts = key.split("\\.", 2);
                NbtCompound nested = nbt.getCompound(parts[0]);
                if (nested.isEmpty() && !nbt.contains(parts[0])) {
            nbt.put(parts[0], nested);
                }
                nested.put(parts[1], value);
       } else {
                nbt.put(key, value);
            }
        }
        for (String key : removals) {
            if (key.contains(".")) {
                String[] parts = key.split("\\.", 2);
                if (nbt.contains(parts[0], 10)) {
                    NbtCompound nested = nbt.getCompound(parts[0]);
                    nested.remove(parts[1]);
                }
            } else {
                nbt.remove(key);
            }
        }
    }
    public static ComponentChanges fromStack(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        Map<String, NbtElement> additions = new HashMap<>();

        if (nbt != null) {

            for (String key : nbt.getKeys()) {
                additions.put(key, nbt.get(key).copy());
            }

            if (nbt.contains("display", 10)) {
                NbtCompound display = nbt.getCompound("display");
                for (String key : display.getKeys()) {
                    additions.put("display." + key, display.get(key).copy());
                }
            }
        }

        return new ComponentChanges(additions, new HashSet<>());
    }


    public static ComponentChanges empty() {
        return new ComponentChanges(new HashMap<>(), new HashSet<>());
    }


    public static Builder builder() {
        return new Builder();
    }
    //builder for chani operations
    public static class Builder {
        private final Map<String, NbtElement> additions = new HashMap<>();
        private final Set<String> removals = new HashSet<>();


        public <T> Builder add(ComponentType<T> type, NbtElement value) {
            additions.put(type.getNbtKey(), value);
            removals.remove(type.getNbtKey());
            return this;
        }


        public Builder add(String key, NbtElement value) {
            additions.put(key, value);
            removals.remove(key);
            return this;
        }


        public Builder addInt(String key, int value) {
            NbtCompound temp = new NbtCompound();
            temp.putInt("temp", value);
            return add(key, temp.get("temp"));
        }


        public Builder addString(String key, String value) {
            NbtCompound temp = new NbtCompound();
            temp.putString("temp", value);
            return add(key, temp.get("temp"));
        }


        public Builder addBoolean(String key, boolean value) {
            NbtCompound temp = new NbtCompound();
            temp.putBoolean("temp", value);
            return add(key, temp.get("temp"));
        }
        public Builder remove(ComponentType<?> type) {
            removals.add(type.getNbtKey());
            additions.remove(type.getNbtKey());
            return this;
        }


        public Builder remove(String key) {
            removals.add(key);
            additions.remove(key);
            return this;
        }


        public ComponentChanges build() {
            return new ComponentChanges(new HashMap<>(additions), new HashSet<>(removals));
        }
    }

    @Override
    public String toString() {
        return "ComponentChanges{additions=" + additions.keySet() + ", removals=" + removals + "}";
    }
}