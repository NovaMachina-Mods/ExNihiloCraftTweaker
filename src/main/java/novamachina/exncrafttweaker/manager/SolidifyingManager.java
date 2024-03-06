package novamachina.exncrafttweaker.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.item.crafting.RecipeType;
import novamachina.exncrafttweaker.EXNCrTHelper;
import novamachina.exnihilosequentia.world.item.crafting.EXNRecipeTypes;
import novamachina.exnihilosequentia.world.item.crafting.SolidifyingRecipe;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.exnihilosequentia.SolidifyingRecipe")
@Document("mods/ExNihiloSequentia/Solidifying")
public class SolidifyingManager implements IRecipeManager<SolidifyingRecipe> {

  @ZenCodeType.Method
  public void addRecipe(
      String name, IFluidStack fluidInTank, IFluidStack fluidOnTop, IItemStack result) {
    name = fixRecipeName(name);
    SolidifyingRecipe recipe =
        new SolidifyingRecipe(
            fluidInTank.getInternal(),
            fluidOnTop.getInternal(),
            result.getInternal());

    ActionAddRecipe<SolidifyingRecipe> actionAddRecipe = new ActionAddRecipe<>(this, createHolder(EXNCrTHelper.resourceLocation(name),recipe));
    actionAddRecipe.outputDescriber(inputRecipe -> String.format("%s", result.getCommandString()));

    CraftTweakerAPI.apply(actionAddRecipe);
  }

  @Override
  public RecipeType<SolidifyingRecipe> getRecipeType() {
    return EXNRecipeTypes.SOLIDIFYING;
  }
}
