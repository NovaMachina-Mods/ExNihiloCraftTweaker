package novamachina.exncrafttweaker.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.world.item.crafting.RecipeType;
import novamachina.exncrafttweaker.EXNCrTHelper;
import novamachina.exnihilosequentia.world.item.crafting.CompostRecipe;
import novamachina.exnihilosequentia.world.item.crafting.EXNRecipeTypes;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.exnihilosequentia.CompostRecipe")
@Document("mods/ExNihiloSequentia/Compost")
public class CompostManager implements IRecipeManager<CompostRecipe> {

  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient input, int amount) {
    name = fixRecipeName(name);
    CompostRecipe recipe =
        new CompostRecipe(input.asVanillaIngredient(), amount);

    ActionAddRecipe<CompostRecipe> actionAddRecipe = new ActionAddRecipe<>(this, createHolder(EXNCrTHelper.resourceLocation(name), recipe));
    actionAddRecipe.outputDescriber(
        inputRecipe -> String.format("%d solids", inputRecipe.value().getAmount()));

    CraftTweakerAPI.apply(actionAddRecipe);
  }

  @Override
  public RecipeType<CompostRecipe> getRecipeType() {
    return EXNRecipeTypes.COMPOST;
  }
}
