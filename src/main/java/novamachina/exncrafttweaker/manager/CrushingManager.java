package novamachina.exncrafttweaker.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import novamachina.exncrafttweaker.EXNCrTHelper;
import novamachina.exnihilosequentia.world.item.crafting.CrushingRecipe;
import novamachina.exnihilosequentia.world.item.crafting.EXNRecipeTypes;
import novamachina.exnihilosequentia.world.item.crafting.ItemStackWithChance;
import org.openzen.zencode.java.ZenCodeType;

import java.util.List;
import java.util.StringJoiner;

@ZenRegister
@ZenCodeType.Name("mods.exnihilosequentia.CrushingRecipe")
@Document("mods/ExNihiloSequentia/Crushing")
public class CrushingManager implements IRecipeManager<CrushingRecipe> {

  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient input, ItemStackWithChance[] drops) {
    name = fixRecipeName(name);
    CrushingRecipe recipe =
        new CrushingRecipe(input.asVanillaIngredient(), List.of(drops));

    ActionAddRecipe<CrushingRecipe> actionAddRecipe = new ActionAddRecipe<>(this, createHolder(EXNCrTHelper.resourceLocation(name), recipe));
    actionAddRecipe.outputDescriber(
        inputRecipe -> {
          StringJoiner dropJoiner = new StringJoiner(", ");
          for (ItemStackWithChance drop : inputRecipe.value().getDrops()) {
            dropJoiner.add(
                String.format(
                    "%dx %s at %f chance",
                    drop.getStack().getCount(),
                    BuiltInRegistries.ITEM.getKey(drop.getStack().getItem()),
                    drop.getChance()));
          }
          return String.format("[%s]", dropJoiner);
        });

    CraftTweakerAPI.apply(actionAddRecipe);
  }

  @Override
  public RecipeType<CrushingRecipe> getRecipeType() {
    return EXNRecipeTypes.CRUSHING;
  }
}
