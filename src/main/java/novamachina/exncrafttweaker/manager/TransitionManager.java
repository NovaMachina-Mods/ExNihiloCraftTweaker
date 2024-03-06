package novamachina.exncrafttweaker.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.item.crafting.RecipeType;
import novamachina.exncrafttweaker.EXNCrTHelper;
import novamachina.exnihilosequentia.world.item.crafting.EXNRecipeTypes;
import novamachina.exnihilosequentia.world.item.crafting.TransitionRecipe;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.exnihilosequentia.TransitionRecipe")
@Document("mods/ExNihiloSequentia/Transition")
public class TransitionManager implements IRecipeManager<TransitionRecipe> {

  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient catalyst, IFluidStack input, IFluidStack output) {
    name = fixRecipeName(name);
    TransitionRecipe recipe =
        new TransitionRecipe(
            catalyst.asVanillaIngredient(),
            input.getInternal(),
            output.getInternal());

    ActionAddRecipe<TransitionRecipe> actionAddRecipe = new ActionAddRecipe<>(this, createHolder(EXNCrTHelper.resourceLocation(name),recipe));
    actionAddRecipe.outputDescriber(inputRecipe -> String.format("%s", output.getCommandString()));

    CraftTweakerAPI.apply(actionAddRecipe);
  }

  @Override
  public RecipeType<TransitionRecipe> getRecipeType() {

    return EXNRecipeTypes.TRANSITION;
  }
}
