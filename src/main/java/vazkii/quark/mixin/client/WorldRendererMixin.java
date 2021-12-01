package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;
import vazkii.quark.base.item.QuarkMusicDiscItem;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {

	@Inject(method = "playRecord(Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/MusicDiscItem;)V",
			remap = false,
			at = @At(value = "JUMP", ordinal = 1),
			locals = LocalCapture.CAPTURE_FAILSOFT,
			cancellable = true)
	public void playRecord(SoundEvent soundIn, BlockPos pos, RecordItem musicDiscItem, CallbackInfo info) {
		if(musicDiscItem instanceof QuarkMusicDiscItem && ((QuarkMusicDiscItem) musicDiscItem).playAmbientSound(pos))
			info.cancel();
	}


}
