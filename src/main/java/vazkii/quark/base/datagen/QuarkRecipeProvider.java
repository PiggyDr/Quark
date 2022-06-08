package vazkii.quark.base.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import vazkii.quark.base.module.ModuleLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

// Taken with love from PAUCAL
public class QuarkRecipeProvider extends RecipeProvider {
	public final String modid;

	public QuarkRecipeProvider(DataGenerator pGenerator, String modid) {
		super(pGenerator);
		this.modid = modid;
	}

	@Override
	protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> recipes) {
		ModuleLoader.INSTANCE.dataGen(this, recipes);
	}

	public ShapedRecipeBuilder ring(ItemLike out, int count, Ingredient outer, @Nullable Ingredient inner) {
		return ringCornered(out, count, outer, outer, inner);
	}

	public ShapedRecipeBuilder ring(ItemLike out, int count, ItemLike outer, @Nullable ItemLike inner) {
		return ring(out, count, Ingredient.of(outer), Ingredient.of(inner));
	}

	public ShapedRecipeBuilder ring(ItemLike out, int count, TagKey<Item> outer, @Nullable TagKey<Item> inner) {
		return ring(out, count, Ingredient.of(outer), inner == null ? null : Ingredient.of(inner));
	}

	public ShapedRecipeBuilder ringCornerless(ItemLike out, int count, Ingredient outer,
												 @Nullable Ingredient inner) {
		return ringCornered(out, count, outer, null, inner);
	}

	public ShapedRecipeBuilder ringCornerless(ItemLike out, int count, ItemLike outer, @Nullable ItemLike inner) {
		return ringCornerless(out, count, Ingredient.of(outer), Ingredient.of(inner));
	}

	public ShapedRecipeBuilder ringCornerless(ItemLike out, int count, TagKey<Item> outer,
												 @Nullable TagKey<Item> inner) {
		return ringCornerless(out, count, Ingredient.of(outer), inner == null ? null : Ingredient.of(inner));
	}

	public ShapedRecipeBuilder ringCornered(ItemLike out, int count, @Nullable Ingredient cardinal,
											   @Nullable Ingredient diagonal, @Nullable Ingredient inner) {
		if (cardinal == null && diagonal == null && inner == null) {
			throw new IllegalArgumentException("at least one ingredient must be non-null");
		}
		if (inner != null && cardinal == null && diagonal == null) {
			throw new IllegalArgumentException("if inner is non-null, either cardinal or diagonal must not be");
		}

		var builder = ShapedRecipeBuilder.shaped(out, count);
		var C = ' ';
		if (cardinal != null) {
			builder.define('C', cardinal);
			C = 'C';
		}
		var D = ' ';
		if (diagonal != null) {
			builder.define('D', diagonal);
			D = 'D';
		}
		var I = ' ';
		if (inner != null) {
			builder.define('I', inner);
			I = 'I';
		}

		builder
				.pattern(String.format("%c%c%c", D, C, D))
				.pattern(String.format("%c%c%c", C, I, C))
				.pattern(String.format("%c%c%c", D, C, D));

		return builder;
	}

	public ShapedRecipeBuilder ringCornered(ItemLike out, int count, @Nullable ItemLike cardinal,
											   @Nullable ItemLike diagonal, @Nullable ItemLike inner) {
		return ringCornered(out, count, ingredientOf(cardinal), ingredientOf(diagonal), ingredientOf(inner));
	}

	public ShapedRecipeBuilder ringCornered(ItemLike out, int count, @Nullable TagKey<Item> cardinal,
											   @Nullable TagKey<Item> diagonal, @Nullable TagKey<Item> inner) {
		return ringCornered(out, count, ingredientOf(cardinal), ingredientOf(diagonal), ingredientOf(inner));
	}

	public ShapedRecipeBuilder stack(ItemLike out, int count, Ingredient top, Ingredient bottom) {
		return ShapedRecipeBuilder.shaped(out, count)
				.define('T', top)
				.define('B', bottom)
				.pattern("T")
				.pattern("B");
	}

	public ShapedRecipeBuilder stack(ItemLike out, int count, ItemLike top, ItemLike bottom) {
		return stack(out, count, Ingredient.of(top), Ingredient.of(bottom));
	}

	public ShapedRecipeBuilder stack(ItemLike out, int count, TagKey<Item> top, TagKey<Item> bottom) {
		return stack(out, count, Ingredient.of(top), Ingredient.of(bottom));
	}

	/**
	 * @param largeSize True for a 3x3, false for a 2x2
	 */
	public void packing(Item free, Item compressed, String freeName, boolean largeSize,
						   Consumer<FinishedRecipe> recipes) {
		var pack = ShapedRecipeBuilder.shaped(compressed)
				.define('X', free);
		if (largeSize) {
			pack.pattern("XXX").pattern("XXX").pattern("XXX");
		} else {
			pack.pattern("XX").pattern("XX").pattern("XX");
		}
		pack.unlockedBy("has_item", has(free)).save(recipes, modLoc(freeName + "_packing"));

		ShapelessRecipeBuilder.shapeless(free, largeSize ? 9 : 4)
				.requires(compressed)
				.unlockedBy("has_item", has(free)).save(recipes, modLoc(freeName + "_unpacking"));
	}

	public ResourceLocation modLoc(String path) {
		return new ResourceLocation(modid, path);
	}

	@Nullable
	public Ingredient ingredientOf(@Nullable ItemLike item) {
		return item == null ? null : Ingredient.of(item);
	}

	@Nullable
	public Ingredient ingredientOf(@Nullable TagKey<Item> item) {
		return item == null ? null : Ingredient.of(item);
	}

	@Nonnull
	@Override
	public String getName() {
		return "Quark Recipes";
	}
}