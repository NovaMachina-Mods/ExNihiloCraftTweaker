package novamachina.exncrafttweaker;

import net.minecraft.resources.ResourceLocation;

public class EXNCrTHelper {
  private EXNCrTHelper() {}
  public static ResourceLocation resourceLocation(String shortId) {
    return new ResourceLocation("exnihilosequentia_ct", shortId);
  }
}
