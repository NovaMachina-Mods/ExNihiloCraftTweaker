package novamachina.exncrafttweaker.handler;

import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.component.IRecipeComponent;
import com.blamejared.crafttweaker.api.recipe.component.RecipeComponentEqualityCheckers;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import novamachina.exnihilosequentia.ExNihiloSequentia;
import novamachina.exnihilosequentia.world.item.crafting.HeatRecipe;

@IRecipeHandler.For(HeatRecipe.class)
public class HeatRecipeHandler implements IRecipeHandler<HeatRecipe> {

  private String encodeProperties(Optional<StatePropertiesPredicate> properties) {
    StringBuilder builder = new StringBuilder("StatePropertiesPredicate.create()");
    if (properties.isPresent()) {
      Optional<JsonElement> props =
          StatePropertiesPredicate.CODEC.encodeStart(JsonOps.INSTANCE, properties.get()).result();
      for (Map.Entry<String, JsonElement> entry : props.get().getAsJsonObject().entrySet()) {
        builder.append(
            String.format(".property(\"%s\", <appropriate property value>)", entry.getKey()));
      }
    }
    builder.append(".build()");
    return builder.toString();
  }

  @Override
  public String dumpToCommandString(
      IRecipeManager<? super HeatRecipe> iRecipeManager,
      RegistryAccess registryAccess,
      RecipeHolder<HeatRecipe> recipeHolder) {
    ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(recipeHolder.value().getInputBlock());
    return String.format(
        "<recipetype:exnihilosequentia:heat>.addRecipe(%s, %d, %s, %s);",
        StringUtil.quoteAndEscape(recipeHolder.id()),
        recipeHolder.value().getAmount(),
        String.format("<block:%s:%s>", blockId.getNamespace(), blockId.getPath()),
        encodeProperties(recipeHolder.value().getProperties()));
  }

  @Override
  public <U extends Recipe<?>> boolean doesConflict(
      IRecipeManager<? super HeatRecipe> manager, HeatRecipe firstRecipe, U secondRecipe) {
    HeatRecipe second = (HeatRecipe) secondRecipe;
    return firstRecipe.getInputBlock().defaultBlockState().is(second.getInputBlock())
        && compareProperties(firstRecipe.getProperties(), second.getProperties());
  }

  @Override
  public Optional<IDecomposedRecipe> decompose(
      IRecipeManager<? super HeatRecipe> iRecipeManager,
      RegistryAccess registryAccess,
      HeatRecipe recipe) {

    IDecomposedRecipe decomposition =
        IDecomposedRecipe.builder()
            .with(BLOCK_INPUT, recipe.getInputBlock())
            .with(OUTPUT_AMOUNT, recipe.getAmount())
            .with(PROPERTIES, recipe.getProperties())
            .build();
    return Optional.of(decomposition);
  }

  @Override
  public Optional<HeatRecipe> recompose(
      IRecipeManager<? super HeatRecipe> iRecipeManager,
      RegistryAccess registryAccess,
      IDecomposedRecipe recipe) {

    Block input = recipe.getOrThrowSingle(BLOCK_INPUT);
    int amount = recipe.getOrThrowSingle(OUTPUT_AMOUNT);
    Optional<StatePropertiesPredicate> properties = recipe.getOrThrowSingle(PROPERTIES);
    if (input == Blocks.AIR) {
      throw new IllegalArgumentException("Invalid input block: is air block");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Invalid heat value: " + amount);
    }
    return Optional.of(new HeatRecipe(input, amount, properties));
  }

  private boolean compareProperties(
      Optional<StatePropertiesPredicate> first, Optional<StatePropertiesPredicate> second) {
    JsonObject firstProps =
        StatePropertiesPredicate.CODEC
            .encodeStart(JsonOps.INSTANCE, first.get())
            .result()
            .get()
            .getAsJsonObject();
    JsonObject secondProps =
        StatePropertiesPredicate.CODEC
            .encodeStart(JsonOps.INSTANCE, second.get())
            .result()
            .get()
            .getAsJsonObject();
    for (Map.Entry<String, JsonElement> entry : firstProps.entrySet()) {
      if (!secondProps.has(entry.getKey())) {
        return false;
      }
      if (!secondProps.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }

    for (Map.Entry<String, JsonElement> entry : secondProps.entrySet()) {
      if (!firstProps.has(entry.getKey())) {
        return false;
      }
      if (!firstProps.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }
    return true;
  }

  IRecipeComponent<Block> BLOCK_INPUT =
      IRecipeComponent.simple(
          new ResourceLocation(ExNihiloSequentia.MOD_ID, "input/block_input"),
          new TypeToken<>() {},
          (block, block2) -> block.defaultBlockState().is(block2));

  IRecipeComponent<Integer> OUTPUT_AMOUNT =
      IRecipeComponent.simple(
          new ResourceLocation(ExNihiloSequentia.MOD_ID, "output/amount"),
          new TypeToken<>() {},
          RecipeComponentEqualityCheckers::areNumbersEqual);

  IRecipeComponent<Optional<StatePropertiesPredicate>> PROPERTIES =
      IRecipeComponent.simple(
          new ResourceLocation(ExNihiloSequentia.MOD_ID, "meta/properties"),
          new TypeToken<>() {},
          this::compareProperties);
}
