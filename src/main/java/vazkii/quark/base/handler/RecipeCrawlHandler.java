package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.api.event.RecipeCrawlEvent;
import vazkii.quark.api.event.RecipeCrawlEvent.Visit;
import vazkii.quark.base.Quark;

@EventBusSubscriber(bus = Bus.FORGE, modid = Quark.MOD_ID)
public class RecipeCrawlHandler {

	private static List<Recipe<?>> recipesToLazyDigest = new ArrayList<>();
	private static Multimap<Item, ItemStack> recipeDigestion = HashMultimap.create();
	private static Multimap<Item, ItemStack> backwardsDigestion = HashMultimap.create();

	private static Object mutex = new Object();
	private static boolean needsCrawl = false;
	
	@SubscribeEvent
	public static void addListener(AddReloadListenerEvent event) {
		event.addListener((barrier, manager, prepFiller, applyFiller, prepExec, applyExec) -> {
			return 
				CompletableFuture.runAsync(() -> {
					clear();
				}, prepExec)

				.thenCompose(barrier::wait)

				.thenRunAsync(() -> {
					needsCrawl = true;
				}, applyExec);
		});
	}

	private static void clear() {
		MinecraftForge.EVENT_BUS.post(new RecipeCrawlEvent.Reset());
	}

	private static void load(RegistryAccess access, RecipeManager manager) {
		if(!manager.getRecipes().isEmpty()) {
			MinecraftForge.EVENT_BUS.post(new RecipeCrawlEvent.CrawlStarting());

			recipesToLazyDigest.clear();
			recipeDigestion.clear();
			backwardsDigestion.clear();
			
			Collection<Recipe<?>> recipes = manager.getRecipes();

			for(Recipe<?> recipe : recipes) {
				if(recipe == null || recipe.getIngredients() == null || recipe.getResultItem(access) == null)
					continue;

				RecipeCrawlEvent.Visit<?> event;

				if(recipe instanceof ShapedRecipe sr)
					event = new Visit.Shaped(access, sr);
				else if(recipe instanceof ShapelessRecipe sr)
					event = new Visit.Shapeless(access, sr);
				else if(recipe instanceof CustomRecipe cr)
					event = new Visit.Custom(access, cr);
				else if(recipe instanceof AbstractCookingRecipe acr)
					event = new Visit.Cooking(access, acr);
				else 
					event = new Visit.Misc(access, recipe);

				recipesToLazyDigest.add(recipe);
				MinecraftForge.EVENT_BUS.post(event);
			}
		}
	}
	
	@SubscribeEvent
	public static void onTick(ServerTickEvent tick) {
		synchronized(mutex) {
			RegistryAccess access = tick.getServer().registryAccess();
			
			if(needsCrawl) {
				RecipeManager manager = tick.getServer().getRecipeManager();
				load(access, manager);
				needsCrawl = false;
			}
			
			if(!recipesToLazyDigest.isEmpty()) {
				recipeDigestion.clear();
				backwardsDigestion.clear();
				
				for(Recipe<?> recipe : recipesToLazyDigest)
					digest(access, recipe);
				
				recipesToLazyDigest.clear();
				MinecraftForge.EVENT_BUS.post(new RecipeCrawlEvent.Digest(recipeDigestion, backwardsDigestion));
			}
		}
	}

	private static void digest(RegistryAccess access, Recipe<?> recipe) {
		ItemStack out = recipe.getResultItem(access);
		Item outItem = out.getItem();

		NonNullList<Ingredient> ingredients = recipe.getIngredients();
		for(Ingredient ingredient : ingredients) {
			for (ItemStack inStack : ingredient.getItems()) {
				recipeDigestion.put(inStack.getItem(), out);
				backwardsDigestion.put(outItem, inStack);
			}
		}
	}

	/*
	 * Derivation list -> items to add and then derive (raw materials)
	 * Whitelist -> items to add and not derive from
	 * Blacklist -> items to ignore
	 */

	public static void recursivelyFindCraftedItemsFromStrings(@Nullable Collection<String> derivationList, @Nullable Collection<String> whitelist, @Nullable Collection<String> blacklist, Consumer<Item> callback) {
		List<Item> parsedDerivationList = derivationList == null ? null : MiscUtil.massRegistryGet(derivationList, ForgeRegistries.ITEMS);
		List<Item> parsedWhitelist      = whitelist == null      ? null : MiscUtil.massRegistryGet(whitelist, ForgeRegistries.ITEMS);
		List<Item> parsedBlacklist      = blacklist == null      ? null : MiscUtil.massRegistryGet(blacklist, ForgeRegistries.ITEMS);

		recursivelyFindCraftedItems(parsedDerivationList, parsedWhitelist, parsedBlacklist, callback);
	}

	public static void recursivelyFindCraftedItems(@Nullable Collection<Item> derivationList, @Nullable Collection<Item> whitelist, @Nullable Collection<Item> blacklist, Consumer<Item> callback) {
		Collection<Item> trueDerivationList = derivationList == null  ? Lists.newArrayList() : derivationList;
		Collection<Item> trueWhitelist      = whitelist == null       ? Lists.newArrayList() : whitelist;
		Collection<Item> trueBlacklist      = blacklist == null       ? Lists.newArrayList() : blacklist;

		Streams.concat(trueDerivationList.stream(), trueWhitelist.stream()).forEach(callback);

		Set<Item> scanned = Sets.newHashSet(trueDerivationList);
		List<Item> toScan = Lists.newArrayList(trueDerivationList);

		while (!toScan.isEmpty()) {
			Item scan = toScan.remove(0);

			if (recipeDigestion.containsKey(scan)) {
				for (ItemStack digestedStack : recipeDigestion.get(scan)) {
					Item candidate = digestedStack.getItem();

					if (!scanned.contains(candidate)) {
						scanned.add(candidate);
						toScan.add(candidate);

						if(!trueBlacklist.contains(candidate))
							callback.accept(candidate);
					}
				}
			}
		}
	}

}
