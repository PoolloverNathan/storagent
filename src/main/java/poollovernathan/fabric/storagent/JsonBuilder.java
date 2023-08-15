package poollovernathan.fabric.storagent;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.function.Function;

public class JsonBuilder {
    protected JsonObject obj = new JsonObject();
    public JsonBuilder add(String key, JsonElement value) {
        obj.add(key, value);
        return this;
    }
    public JsonBuilder add(String key, Number value) {
        obj.addProperty(key, value);
        return this;
    }
    public JsonBuilder add(String key, String value) {
        obj.addProperty(key, value);
        return this;
    }
    public JsonBuilder add(String key, boolean value) {
        obj.addProperty(key, value);
        return this;
    }
    public JsonBuilder add(String key, char value) {
        obj.addProperty(key, value);
        return this;
    }
    public JsonBuilder add(String key, JsonBuilder builder) {
        return add(key, builder.build());
    }
    public JsonBuilder add(String key, Function<JsonBuilder, ?> filler) {
        var builder = new JsonBuilder();
        filler.apply(builder);
        return add(key, builder);
    }
    public JsonElement build() {
        return obj;
    }
}
