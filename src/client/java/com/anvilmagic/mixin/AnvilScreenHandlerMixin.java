package com.anvilmagic.mixin;

import com.anvilmagic.AnvilMagicClient;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    
    @Shadow @Final private Property levelCost;
    
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftStack = handler.getSlot(0).getStack();
        ItemStack rightStack = handler.getSlot(1).getStack();
        
        // 检查是否为拆分附魔书的操作
        if (shouldSplitEnchantedBook(leftStack, rightStack)) {
            handleEnchantmentSplitting(handler, leftStack);
            ci.cancel();
        }
    }
    
    private boolean shouldSplitEnchantedBook(ItemStack leftStack, ItemStack rightStack) {
        // 左槽为附魔书，右槽为空或者是普通书
        return leftStack.isOf(Items.ENCHANTED_BOOK) && 
               (rightStack.isEmpty() || rightStack.isOf(Items.BOOK)) &&
               !EnchantmentHelper.getEnchantments(leftStack).isEmpty();
    }
    
    private void handleEnchantmentSplitting(AnvilScreenHandler handler, ItemStack enchantedBook) {
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(enchantedBook);
        Set<RegistryEntry<Enchantment>> enchantmentSet = enchantments.getEnchantments();
        
        // 如果只有一个附魔，无法拆分
        if (enchantmentSet.size() <= 1) {
            handler.getSlot(2).setStack(ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }
        
        // 创建新的附魔书，只包含第一个附魔
        ItemStack newEnchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
        RegistryEntry<Enchantment> firstEnchantment = enchantmentSet.iterator().next();
        int level = enchantments.getLevel(firstEnchantment);
        
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        builder.add(firstEnchantment, level);
        EnchantmentHelper.set(newEnchantedBook, builder.build());
        
        // 设置结果槽
        handler.getSlot(2).setStack(newEnchantedBook);
        
        // 设置经验消耗为1级
        this.levelCost.set(1);
        
        AnvilMagicClient.LOGGER.info("准备拆分附魔书: {} -> {}", 
            enchantmentSet.size(), 1);
    }
}