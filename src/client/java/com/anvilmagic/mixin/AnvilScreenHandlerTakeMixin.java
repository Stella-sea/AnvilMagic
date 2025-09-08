package com.anvilmagic.mixin;

import com.anvilmagic.AnvilMagicClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerTakeMixin {
    
    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftStack = handler.getSlot(0).getStack();
        ItemStack rightStack = handler.getSlot(1).getStack();
        
        // 检查是否是拆分附魔书的操作
        if (shouldProcessSplitting(leftStack, rightStack, stack)) {
            processSplitting(player, handler, leftStack);
        }
    }
    
    private boolean shouldProcessSplitting(ItemStack leftStack, ItemStack rightStack, ItemStack outputStack) {
        return leftStack.isOf(Items.ENCHANTED_BOOK) && 
               (rightStack.isEmpty() || rightStack.isOf(Items.BOOK)) &&
               outputStack.isOf(Items.ENCHANTED_BOOK);
    }
    
    private void processSplitting(PlayerEntity player, AnvilScreenHandler handler, ItemStack originalBook) {
        Map<RegistryEntry<Enchantment>, Integer> originalEnchantments = 
            EnchantmentHelper.getEnchantments(originalBook);
        
        if (originalEnchantments.size() <= 1) {
            return;
        }
        
        // 创建剩余的附魔书
        Map<RegistryEntry<Enchantment>, Integer> remainingEnchantments = new HashMap<>(originalEnchantments);
        RegistryEntry<Enchantment> removedEnchantment = remainingEnchantments.keySet().iterator().next();
        remainingEnchantments.remove(removedEnchantment);
        
        // 如果还有剩余附魔，更新左槽的物品
        if (!remainingEnchantments.isEmpty()) {
            ItemStack newLeftStack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.set(newLeftStack, remainingEnchantments);
            handler.getSlot(0).setStack(newLeftStack);
        } else {
            // 如果没有剩余附魔，清空左槽
            handler.getSlot(0).setStack(ItemStack.EMPTY);
        }
        
        // 给予玩家1级经验
        player.addExperience(7); // 1级经验约等于7经验点
        
        AnvilMagicClient.LOGGER.info("玩家 {} 成功拆分附魔书，获得1级经验。剩余附魔: {}", 
            player.getName().getString(), remainingEnchantments.size());
    }
}