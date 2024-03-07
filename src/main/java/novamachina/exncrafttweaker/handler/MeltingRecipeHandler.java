package novamachina.exncrafttweaker.handler;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.component.BuiltinRecipeComponents;
import com.blamejared.crafttweaker.api.recipe.component.IDecomposedRecipe;
import com.blamejared.crafttweaker.api.recipe.handler.IRecipeHandler;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker.api.util.IngredientUtil;
import com.blamejared.crafttweaker.api.util.StringUtil;
import java.util.Optional;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import novamachina.exnihilosequentia.world.item.crafting.MeltingRecipe;
import novamachina.exnihilosequentia.world.level.block.entity.CrucibleBlockEntity;

@IRecipeHandler.For(MeltingRecipe.class)
public class MeltingRecipeHandler implements IRecipeHandler<MeltingRecipe> {
  @Override
  public String dumpToCommandString(
      IRecipeManager<? super MeltingRecipe> manager,
      RegistryAccess registryAccess,
      RecipeHolder<MeltingRecipe> recipe) {
    return String.format(
        "<recipetype:exnihilosequentia:compost>.addRecipe(%s, %s, %s, %s);",
        StringUtil.quoteAndEscape(recipe.id()),
        IIngredient.fromIngredient(recipe.value().getInput()).getCommandString(),
        IFluidStack.of(recipe.value().getResultFluid()).getCommandString(),
        String.format("CrucibleType.%s()", recipe.value().getCrucibleType().getName()));
  }

  @Override
  public <U extends Recipe<?>> boolean doesConflict(
      IRecipeManager<? super MeltingRecipe> manager, MeltingRecipe firstRecipe, U secondRecipe) {
    MeltingRecipe second = (MeltingRecipe) secondRecipe;
    return IngredientUtil.canConflict(firstRecipe.getInput(), second.getInput());
  }

  @Override
  public Optional<IDecomposedRecipe> decompose(
      IRecipeManager<? super MeltingRecipe> manager,
      RegistryAccess registryAccess,
      MeltingRecipe recipe) {
    IIngredient input = IIngredient.fromIngredient(recipe.getInput());
    IFluidStack fluidStack = IFluidStack.of(recipe.getResultFluid());
    IDecomposedRecipe decomposition =
        IDecomposedRecipe.builder()
            .with(BuiltinRecipeComponents.Input.INGREDIENTS, input)
            .with(BuiltinRecipeComponents.Output.FLUIDS, fluidStack)
            .with(BuiltinRecipeComponents.Metadata.GROUP, recipe.getCrucibleType().getName())
            .build();
    return Optional.of(decomposition);
  }

  @Override
  public Optional<MeltingRecipe> recompose(
      IRecipeManager<? super MeltingRecipe> manager,
      RegistryAccess registryAccess,
      IDecomposedRecipe recipe) {
    IIngredient input = recipe.getOrThrowSingle(BuiltinRecipeComponents.Input.INGREDIENTS);
    IFluidStack fluidStack = recipe.getOrThrowSingle(BuiltinRecipeComponents.Output.FLUIDS);
    CrucibleBlockEntity.CrucibleType type =
        CrucibleBlockEntity.CrucibleType.getTypeByName(
            recipe.getOrThrowSingle(BuiltinRecipeComponents.Metadata.GROUP));

    if (input.isEmpty()) {
      throw new IllegalArgumentException("Invalid input: empty ingredient");
    }
    if (fluidStack.isEmpty()) {
      throw new IllegalArgumentException("Invalid fluid: empty fluid");
    }
    if (type == null) {
      throw new IllegalArgumentException("Invalid type: missing type");
    }

    return Optional.of(
        new MeltingRecipe(input.asVanillaIngredient(), fluidStack.getInternal(), type));
  }
}
